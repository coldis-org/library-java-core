package org.coldis.library.thread;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.coldis.library.helper.RandomHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dynamic thread pool factory.
 */
public class DynamicThreadPoolFactory {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicThreadPoolFactory.class);

	/**
	 * Pool name.
	 */
	private String name;

	/**
	 * Thread priority.
	 */
	private Integer priority;

	/** If it is a scheduled thread pool. */
	private Boolean scheduled;

	/** If virtual threads should be used. */
	private Boolean virtual;

	/**
	 * If daemon threads should be used.
	 */
	private Boolean daemon;

	/** Parallelism. */
	private Integer parallelism;

	/** Parallelism CPU multiplier. */
	private Integer parallelismCpuMultiplier;

	/** Core pool size. */
	private Integer corePoolSize;

	/** Core pool size CPU multiplier. */
	private Double corePoolSizeCpuMultiplier;

	/** Max pool size. */
	private Integer maxPoolSize;

	/** Max pool size CPU multiplier. */
	private Double maxPoolSizeCpuMultiplier;

	/** Max queue size. */
	private Integer maxQueueSize;

	/** Keep alive. */
	private Duration keepAlive;

	/**
	 * Sets the name of the thread pool.
	 *
	 * @param
	 */
	public DynamicThreadPoolFactory withName(
			final String name) {
		this.name = name;
		return this;
	}

	/**
	 * Gets the name.
	 *
	 * @return The name.
	 */
	private String getName() {
		return (this.name == null ? "thread-pool-" + RandomHelper.getPositiveRandomLongAsString(100L) : this.name);
	}

	/**
	 * Sets the priority.
	 *
	 * @param priority New priority.
	 */
	public DynamicThreadPoolFactory withPriority(
			final Integer priority) {
		this.priority = priority;
		return this;
	}

	/**
	 * Gets the priority.
	 *
	 * @return The priority.
	 */
	private Integer getPriority() {
		return (this.priority == null ? Thread.NORM_PRIORITY : this.priority);
	}

	/**
	 * Sets the scheduled.
	 *
	 * @param scheduled New scheduled.
	 */
	public DynamicThreadPoolFactory withScheduled(
			final Boolean scheduled) {
		this.scheduled = scheduled;
		return this;
	}

	/**
	 * Gets the scheduled.
	 *
	 * @return The scheduled.
	 */
	private Boolean getScheduled() {
		return (this.scheduled == null ? false : this.scheduled);
	}

	/**
	 * Sets the virtual.
	 *
	 * @param virtual New virtual.
	 */
	public DynamicThreadPoolFactory withVirtual(
			final Boolean virtual) {
		this.virtual = virtual;
		return this;
	}

	/**
	 * Gets the virtual.
	 *
	 * @return The virtual.
	 */
	private Boolean getVirtual() {
		return (this.virtual == null ? false : this.virtual);
	}

	/**
	 * Sets the daemon.
	 *
	 * @param daemon New daemon.
	 */
	public DynamicThreadPoolFactory withDaemon(
			final Boolean daemon) {
		this.daemon = daemon;
		return this;
	}

	/**
	 * Gets the daemon.
	 *
	 * @return The daemon.
	 */
	private Boolean getDaemon() {
		return (this.daemon == null ? false : this.daemon);
	}

	/**
	 * Sets the parallelism.
	 *
	 * @param parallelism New parallelism.
	 */
	public DynamicThreadPoolFactory withParallelism(
			final Integer parallelism) {
		this.parallelism = parallelism;
		return this;
	}

	/**
	 * Gets the parallelism.
	 *
	 * @return The parallelism.
	 */
	private Integer getParallelism() {
		return this.parallelism;
	}

	/**
	 * Sets the parallelismCpuMultiplier.
	 *
	 * @param parallelismCpuMultiplier New parallelismCpuMultiplier.
	 */
	public DynamicThreadPoolFactory withParallelismCpuMultiplier(
			final Integer parallelismCpuMultiplier) {
		this.parallelismCpuMultiplier = parallelismCpuMultiplier;
		return this;
	}

	/**
	 * Gets the parallelismCpuMultiplier.
	 *
	 * @return The parallelismCpuMultiplier.
	 */
	private Integer getParallelismCpuMultiplier() {
		return this.parallelismCpuMultiplier;
	}

	/**
	 * Sets the corePoolSize.
	 *
	 * @param corePoolSize New corePoolSize.
	 */
	public DynamicThreadPoolFactory withCorePoolSize(
			final Integer corePoolSize) {
		this.corePoolSize = corePoolSize;
		return this;
	}

	/**
	 * Gets the core pool size.
	 *
	 * @return The core pool size.
	 */
	private Integer getCorePoolSize() {
		return this.corePoolSize;
	}

	/**
	 * Sets the corePoolSizeCpuMultiplier.
	 *
	 * @param corePoolSizeCpuMultiplier New corePoolSizeCpuMultiplier.
	 */
	public DynamicThreadPoolFactory withCorePoolSizeCpuMultiplier(
			final Double corePoolSizeCpuMultiplier) {
		this.corePoolSizeCpuMultiplier = corePoolSizeCpuMultiplier;
		return this;
	}

	/**
	 * Gets the core pool size CPU multiplier.
	 *
	 * @return The core pool size CPU multiplier.
	 */
	private Double getCorePoolSizeCpuMultiplier() {
		return (this.corePoolSizeCpuMultiplier == null ? 1 : this.corePoolSizeCpuMultiplier);
	}

	/**
	 * Sets the maxPoolSize.
	 *
	 * @param maxPoolSize New maxPoolSize.
	 */
	public DynamicThreadPoolFactory withMaxPoolSize(
			final Integer maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		return this;
	}

	/**
	 * Gets the max pool size.
	 *
	 * @return The max pool size.
	 */
	private Integer getMaxPoolSize() {
		return this.maxPoolSize;
	}

	/**
	 * Sets the maxPoolSizeCpuMultiplier.
	 *
	 * @param maxPoolSizeCpuMultiplier New maxPoolSizeCpuMultiplier.
	 */
	public DynamicThreadPoolFactory withMaxPoolSizeCpuMultiplier(
			final Double maxPoolSizeCpuMultiplier) {
		this.maxPoolSizeCpuMultiplier = maxPoolSizeCpuMultiplier;
		return this;
	}

	/**
	 * Gets the max pool size CPU multiplier.
	 *
	 * @return The max pool size CPU multiplier.
	 */
	private Double getMaxPoolSizeCpuMultiplier() {
		return (this.maxPoolSizeCpuMultiplier == null ? 1 : this.maxPoolSizeCpuMultiplier);
	}

	/**
	 * Sets the maxQueueSize.
	 *
	 * @param maxQueueSize New maxQueueSize.
	 */
	public DynamicThreadPoolFactory withMaxQueueSize(
			final Integer maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
		return this;
	}

	/**
	 * Gets the max queue size.
	 *
	 * @return The max queue size.
	 */
	private Integer getMaxQueueSize() {
		return (this.maxQueueSize == null ? Integer.MAX_VALUE : this.maxQueueSize);
	}

	/**
	 * Sets the keepAlive.
	 *
	 * @param keepAlive New keepAlive.
	 */
	public DynamicThreadPoolFactory withKeepAlive(
			final Duration keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}

	/**
	 * Gets the keep alive.
	 *
	 * @return The keep alive.
	 */
	private Duration getKeepAlive() {
		return (this.keepAlive == null ? Duration.ofSeconds(60) : this.keepAlive);
	}

	/** Configurable thread factory. */
	static class ConfigurableThreadFactory implements ThreadFactory {

		/**
		 * Thread number.
		 */
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		/**
		 * Delegate.
		 */
		private final ThreadFactory delegate;

		/** Name prefix. */
		private String namePrefix;

		/** Priority. */
		private final Integer priority;

		/**
		 * Delegate constructor.
		 */
		public ConfigurableThreadFactory(final ThreadFactory delegate, final String namePrefix, final Integer priority) {
			super();
			this.delegate = delegate;
			this.priority = priority;
		}

		/**
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		@Override
		public Thread newThread(
				final Runnable runnable) {
			final Thread newThread = this.delegate.newThread(runnable);
			final String name = (this.namePrefix + "-" + this.threadNumber.getAndIncrement());
			newThread.setName(name);
			newThread.setPriority(this.priority);
			return newThread;
		}

	}

	/** Configurable thread factory. */
	static class ConfigurableForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {

		/**
		 * Thread number.
		 */
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		/**
		 * Delegate.
		 */
		private final ForkJoinWorkerThreadFactory delegate;

		/** Name prefix. */
		private String namePrefix;

		/** Priority. */
		private final Integer priority;

		/**
		 * Delegate constructor.
		 */
		public ConfigurableForkJoinWorkerThreadFactory(final ForkJoinWorkerThreadFactory delegate, final String namePrefix, final Integer priority) {
			super();
			this.delegate = delegate;
			this.priority = priority;
		}

		/**
		 * @see java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory#newThread(java.util.concurrent.ForkJoinPool)
		 */
		@Override
		public ForkJoinWorkerThread newThread(
				final ForkJoinPool pool) {
			final ForkJoinWorkerThread newThread = this.delegate.newThread(pool);
			final String name = (this.namePrefix + "-" + this.threadNumber.getAndIncrement());
			newThread.setName(name);
			newThread.setPriority(this.priority);
			return newThread;
		}

	}

	/**
	 * Builds the thread pool.
	 *
	 * @return The created thread pool.
	 */
	public Executor build() {
		Executor executor = null;
		// Gets the parameters.
		final Integer actualParallelism = ((this.getParallelism() == null) || (this.getParallelism() < 0)
				? (this.getParallelismCpuMultiplier() == null ? null
						: ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * this.getParallelismCpuMultiplier())).intValue())
				: this.getParallelism());
		final Integer actualCorePoolSize = ((this.getCorePoolSize() == null) || (this.getCorePoolSize() < 0)
				? ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * this.getCorePoolSizeCpuMultiplier())).intValue()
				: this.getCorePoolSize());
		final Integer actualMaxPoolSize = ((this.getMaxPoolSize() == null) || (this.getMaxPoolSize() < 0)
				? ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * this.getMaxPoolSizeCpuMultiplier())).intValue()
				: this.getMaxPoolSize());
		final Long actualKeepAliveMillis = this.getKeepAlive().toMillis();
		final TimeUnit actualKeepAliveUnit = TimeUnit.MILLISECONDS;
		final ThreadFactory factory = (this.getVirtual() ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory());

		// If parallelism is set.
		if (actualParallelism != null) {
			final ForkJoinPool forkJoinPool = new ForkJoinPool(actualParallelism,
					new ConfigurableForkJoinWorkerThreadFactory(ForkJoinPool.defaultForkJoinWorkerThreadFactory, this.getName(), this.getPriority()), null,
					true, actualParallelism, actualMaxPoolSize, actualParallelism / 2, null, actualKeepAliveMillis, actualKeepAliveUnit);
			executor = forkJoinPool;
		}

		// If it is a scheduled thread pool.
		else if (this.getScheduled()) {
			final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(actualCorePoolSize,
					new ConfigurableThreadFactory(factory, this.getName(), this.getPriority()));
			executor = scheduledThreadPoolExecutor;
		}

		// Otherwise, uses a common pool.
		else {
			final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(this.getMaxQueueSize());
			final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(actualCorePoolSize, actualMaxPoolSize, this.keepAlive.toMillis(),
					TimeUnit.MILLISECONDS, queue, new ConfigurableThreadFactory(factory, this.getName(), this.getPriority()));
			threadPoolExecutor.allowCoreThreadTimeOut(true);
			executor = threadPoolExecutor;
		}

		// Returns the executor.
		DynamicThreadPoolFactory.LOGGER.info("Thread pool '" + this.getName() + "' created: '" + ToStringBuilder.reflectionToString(this) + "'.");
		return executor;
	}

}
