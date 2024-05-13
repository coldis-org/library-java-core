package org.coldis.library.helper;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Computing resources helper.
 */
public class ComputingResourcesHelper {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EnumHelper.class);

	/**
	 * CPU quota unit.
	 */
	public static final Long CPU_QUOTA_UNIT = 100000L;

	/**
	 * Gets the SPU quota available.
	 *
	 * @return The SPU quota available.
	 */
	public static Long getCpuQuota(
			final Boolean tryCgroup) {
		Long cpuQuota = (Runtime.getRuntime().availableProcessors() * ComputingResourcesHelper.CPU_QUOTA_UNIT);
		if (tryCgroup) {
			try {
				cpuQuota = Long.valueOf(FileUtils.readFileToString(new File("/sys/fs/cgroup/cpu/cpu.cfs_quota_us"), Charset.defaultCharset()));
			}
			catch (final Exception exception) {
				ComputingResourcesHelper.LOGGER.debug("CPU quota could be red from disk", exception);
			}
		}
		return cpuQuota;
	}

}
