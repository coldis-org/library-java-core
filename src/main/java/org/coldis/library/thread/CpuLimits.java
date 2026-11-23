package org.coldis.library.thread;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The CPU count that CPU-based limits (threads or permits per CPU) are
 * multiplied by, and the resolution of such limits. Plain Java, no framework.
 * <p>
 * CPUs, first match:
 * <ol>
 * <li>the pinned value ({@link #pin(double)}), set once at startup, e.g. by an
 * application from its configuration;</li>
 * <li>the cgroup v2 quota: {@code cpu.max} (quota / period);</li>
 * <li>the cgroup v1 quota: {@code cpu.cfs_quota_us / cpu.cfs_period_us} (under
 * the root or its {@code cpu} / {@code cpu,cpuacct} controllers);</li>
 * <li>{@link Runtime#availableProcessors()}.</li>
 * </ol>
 * A quota is capped at {@code availableProcessors()}. The JVM rounds a
 * fractional quota up (a quota of 2.08 CPUs reports 3 processors), which is why
 * the quota itself is read.
 * <p>
 * A limit resolves, first match: the explicit absolute value (when positive),
 * the explicit ratio per CPU, the default ratio per CPU; a ratio is multiplied
 * by the CPUs and rounded up, with a minimum of 1.
 * <p>
 * Thread safety: an instance is safe for concurrent use. The pinned value can
 * be set once: the first {@link #pin(double)} with a positive value wins, and a
 * later different value is ignored (with a warning), so readers never see the
 * CPU count change after startup. The detected value is read from the cgroup
 * once, on first use, and cached. {@link #getDefault()} is the process-wide
 * instance (the {@code /sys/fs/cgroup} root and the runtime's processors);
 * tests build their own instances instead of pinning it.
 */
public final class CpuLimits {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CpuLimits.class);

	/**
	 * Default cgroup root.
	 */
	public static final Path CGROUP_ROOT = Path.of("/sys/fs/cgroup");

	/**
	 * Process-wide instance.
	 */
	private static final CpuLimits DEFAULT = new CpuLimits(CpuLimits.CGROUP_ROOT, () -> Runtime.getRuntime().availableProcessors());

	/**
	 * Cgroup root.
	 */
	private final Path cgroupRoot;

	/**
	 * Runtime processors.
	 */
	private final IntSupplier runtimeProcessors;

	/**
	 * Lock for pinning and detection.
	 */
	private final Object lock = new Object();

	/**
	 * Pinned CPUs (null until pinned).
	 */
	private volatile Double pinnedCpus;

	/**
	 * Detected CPUs (null until first use).
	 */
	private volatile Double detectedCpus;

	/**
	 * Constructor.
	 *
	 * @param cgroupRoot        Cgroup root.
	 * @param runtimeProcessors Runtime processors.
	 */
	public CpuLimits(final Path cgroupRoot, final IntSupplier runtimeProcessors) {
		this.cgroupRoot = Objects.requireNonNull(cgroupRoot);
		this.runtimeProcessors = Objects.requireNonNull(runtimeProcessors);
	}

	/**
	 * Gets the process-wide instance.
	 *
	 * @return The process-wide instance.
	 */
	public static CpuLimits getDefault() {
		return CpuLimits.DEFAULT;
	}

	/**
	 * Reads a cgroup file, trimmed.
	 *
	 * @param  file File.
	 * @return      The content, or null when it cannot be read.
	 */
	private static String read(
			final Path file) {
		String content = null;
		try {
			content = (Files.isReadable(file) ? StringUtils.trimToNull(Files.readString(file, StandardCharsets.UTF_8)) : null);
		}
		catch (final IOException exception) {
			CpuLimits.LOGGER.debug("Could not read '{}'.", file, exception);
		}
		return content;
	}

	/**
	 * Divides a quota by its period.
	 *
	 * @param  quota  Quota ("max" or negative: none).
	 * @param  period Period.
	 * @return        The CPUs, or null when there is no quota.
	 */
	private static Double divide(
			final String quota,
			final String period) {
		Double cpus = null;
		if ((quota != null) && (period != null) && quota.matches("\\d+") && period.matches("\\d+") && (Long.parseLong(period) > 0)) {
			cpus = Long.parseLong(quota) / (double) Long.parseLong(period);
		}
		return ((cpus == null) || (cpus <= 0) ? null : cpus);
	}

	/**
	 * Detects the CPUs from the cgroup tree: v2 {@code cpu.max}, then v1, capped
	 * at the runtime's processors; without a quota, the runtime's processors.
	 *
	 * @return The CPUs.
	 */
	private double detectCpus() {
		final double processors = Math.max(1, this.runtimeProcessors.getAsInt());
		final String cpuMax = CpuLimits.read(this.cgroupRoot.resolve("cpu.max"));
		Double quota = (cpuMax == null ? null : CpuLimits.divide(StringUtils.substringBefore(cpuMax, " "), StringUtils.substringAfter(cpuMax, " ")));
		for (final String controller : List.of("", "cpu", "cpu,cpuacct")) {
			final Path directory = (controller.isEmpty() ? this.cgroupRoot : this.cgroupRoot.resolve(controller));
			quota = (quota == null
					? CpuLimits.divide(CpuLimits.read(directory.resolve("cpu.cfs_quota_us")), CpuLimits.read(directory.resolve("cpu.cfs_period_us")))
					: quota);
		}
		return (quota == null ? processors : Math.min(quota, processors));
	}

	/**
	 * Pins the CPUs, once: the first positive value wins; a later different value
	 * is ignored with a warning (readers must not see the count change).
	 *
	 * @param  cpus CPUs (positive).
	 * @return      If the CPUs are pinned to this value after the call.
	 */
	public boolean pin(
			final double cpus) {
		boolean pinned = false;
		if (cpus > 0) {
			synchronized (this.lock) {
				if (this.pinnedCpus == null) {
					this.pinnedCpus = cpus;
					CpuLimits.LOGGER.info("CPU-based limits use {} pinned CPUs.", cpus);
				}
				else if (this.pinnedCpus != cpus) {
					CpuLimits.LOGGER.warn("CPU-based limits are already pinned to {} CPUs; ignoring {}.", this.pinnedCpus, cpus);
				}
				pinned = (this.pinnedCpus == cpus);
			}
		}
		return pinned;
	}

	/**
	 * Gets the pinned CPUs.
	 *
	 * @return The pinned CPUs, or null when not pinned.
	 */
	public Double getPinnedCpus() {
		return this.pinnedCpus;
	}

	/**
	 * Gets the CPUs: the pinned value, else the detected one (detected once).
	 *
	 * @return The CPUs.
	 */
	public double getCpus() {
		final Double actualPinnedCpus = this.pinnedCpus;
		if ((actualPinnedCpus == null) && (this.detectedCpus == null)) {
			synchronized (this.lock) {
				if (this.detectedCpus == null) {
					this.detectedCpus = this.detectCpus();
					CpuLimits.LOGGER.info("CPU-based limits use {} CPUs (cgroup quota or runtime processors).", this.detectedCpus);
				}
			}
		}
		return (actualPinnedCpus == null ? this.detectedCpus : actualPinnedCpus);
	}

	/**
	 * Resolves a limit against a CPU count: the absolute value when positive,
	 * else the ratio, else the default ratio, times the CPUs, rounded up, at
	 * least 1.
	 *
	 * @param  absolute     Explicit absolute value (null when not set).
	 * @param  ratio        Explicit ratio per CPU (null when not set).
	 * @param  defaultRatio Default ratio per CPU (null for no default).
	 * @param  cpus         CPU count.
	 * @return              The limit, or null when none applies (no value, or a
	 *                      ratio not positive).
	 */
	public static Integer resolve(
			final Integer absolute,
			final Double ratio,
			final Double defaultRatio,
			final DoubleSupplier cpus) {
		final Double actualRatio = (ratio == null ? defaultRatio : ratio);
		Integer limit = null;
		if ((absolute != null) && (absolute > 0)) {
			limit = absolute;
		}
		else if ((actualRatio != null) && (actualRatio > 0)) {
			limit = Math.max(1, (int) Math.ceil(cpus.getAsDouble() * actualRatio));
		}
		return limit;
	}

	/**
	 * Resolves a limit against {@link #getCpus()}.
	 *
	 * @param  absolute     Explicit absolute value (null when not set).
	 * @param  ratio        Explicit ratio per CPU (null when not set).
	 * @param  defaultRatio Default ratio per CPU (null for no default).
	 * @return              The limit, or null when none applies.
	 * @see                 #resolve(Integer, Double, Double, DoubleSupplier)
	 */
	public Integer resolve(
			final Integer absolute,
			final Double ratio,
			final Double defaultRatio) {
		return CpuLimits.resolve(absolute, ratio, defaultRatio, this::getCpus);
	}

	/**
	 * Resolves a ratio per CPU against {@link #getCpus()}: rounded up, at least 1.
	 *
	 * @param  multiplier Ratio per CPU.
	 * @return            The limit (1 for a ratio that is not positive).
	 */
	public int resolve(
			final double multiplier) {
		return Math.max(1, (int) Math.ceil(this.getCpus() * multiplier));
	}

}
