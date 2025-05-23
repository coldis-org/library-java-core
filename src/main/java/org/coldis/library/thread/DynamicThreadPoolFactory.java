package org.coldis.library.thread;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
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
	private Double parallelismCpuMultiplier;

	/** Minimum runnable. */
	private Integer minRunnable;

	/** Minimum runnable CPU multiplier. */
	private Double minRunnableCpuMultiplier;

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

	/** Fair queue */
	private Boolean fairQueue;

	/** Keep alive. */
	private Duration keepAlive;

	/**
	 * Default constructor.
	 */
	public DynamicThreadPoolFactory() {
		super();
	}

	/**
	 * Constructor with another factory instance.
	 */
	public DynamicThreadPoolFactory(final DynamicThreadPoolFactory factory) {
		super();
		this.name = factory.getName();
		this.priority = factory.getPriority();
		this.scheduled = factory.getScheduled();
		this.virtual = factory.getVirtual();
		this.daemon = factory.getDaemon();
		this.parallelism = factory.getParallelism();
		this.parallelismCpuMultiplier = factory.getParallelismCpuMultiplier();
		this.minRunnable = factory.getMinRunnable();
		this.minRunnableCpuMultiplier = factory.getMinRunnableCpuMultiplier();
		this.corePoolSize = factory.getCorePoolSize();
		this.corePoolSizeCpuMultiplier = factory.getCorePoolSizeCpuMultiplier();
		this.maxPoolSize = factory.getMaxPoolSize();
		this.maxPoolSizeCpuMultiplier = factory.getMaxPoolSizeCpuMultiplier();
		this.maxQueueSize = factory.getMaxQueueSize();
		this.fairQueue = factory.getFairQueue();
		this.keepAlive = factory.getKeepAlive();
	}

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
	 * Sets the minRunnable.
	 *
	 * @param minRunnable New minRunnable.
	 */
	public DynamicThreadPoolFactory withMinRunnable(
			final Integer minRunnable) {
		this.minRunnable = minRunnable;
		return this;
	}

	/**
	 * Gets the minRunnable.
	 *
	 * @return The minRunnable.
	 */
	private Integer getMinRunnable() {
		return (this.minRunnable == null ? (this.getParallelism() == null ? null : this.getParallelism() / 5) : this.minRunnable);
	}

	/**
	 * Sets the minRunnableCpuMultiplier.
	 *
	 * @param minRunnableCpuMultiplier New minRunnableCpuMultiplier.
	 */
	public DynamicThreadPoolFactory withMinRunnableCpuMultiplier(
			final Double minRunnableCpuMultiplier) {
		this.minRunnableCpuMultiplier = minRunnableCpuMultiplier;
		return this;
	}

	/**
	 * Gets the minRunnableCpuMultiplier.
	 *
	 * @return The minRunnableCpuMultiplier.
	 */
	private Double getMinRunnableCpuMultiplier() {
		return (this.minRunnableCpuMultiplier == null ? (this.getParallelismCpuMultiplier() == null ? 1D : this.getParallelismCpuMultiplier() / 5D)
				: this.minRunnableCpuMultiplier);
	}

	/**
	 * Sets the parallelismCpuMultiplier.
	 *
	 * @param parallelismCpuMultiplier New parallelismCpuMultiplier.
	 */
	public DynamicThreadPoolFactory withParallelismCpuMultiplier(
			final Double parallelismCpuMultiplier) {
		this.parallelismCpuMultiplier = parallelismCpuMultiplier;
		return this;
	}

	/**
	 * Gets the parallelismCpuMultiplier.
	 *
	 * @return The parallelismCpuMultiplier.
	 */
	private Double getParallelismCpuMultiplier() {
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
		return this.maxPoolSizeCpuMultiplier;
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
	 * Gets the fairQueue.
	 *
	 * @return The fairQueue.
	 */
	public Boolean getFairQueue() {
		return (this.fairQueue == null ? false : this.fairQueue);
	}

	/**
	 * Sets the fairQueue.
	 *
	 * @param  fairQueue New fairQueue.
	 * @return           The factory.
	 */
	public DynamicThreadPoolFactory withFairQueue(
			final Boolean fairQueue) {
		this.fairQueue = fairQueue;
		return this;
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
		Integer actualParallelism = ((this.getParallelism() == null) || (this.getParallelism() < 0)
				? (this.getParallelismCpuMultiplier() == null ? null
						: ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * this.getParallelismCpuMultiplier())).intValue())
				: this.getParallelism());
		actualParallelism = (actualParallelism == null ? null : Math.max(actualParallelism, 1));
		Integer actualMinRunnable = ((this.getMinRunnable() == null) || (this.getMinRunnable() < 0)
				? (((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * this.getMinRunnableCpuMultiplier())).intValue())
				: this.getMinRunnable());
		actualMinRunnable = (actualMinRunnable == null ? null : Math.max(actualMinRunnable, 1));
		Integer actualCorePoolSize = ((this.getCorePoolSize() == null) || (this.getCorePoolSize() < 0)
				? ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * this.getCorePoolSizeCpuMultiplier())).intValue()
				: this.getCorePoolSize());
		actualCorePoolSize = (actualParallelism == null ? actualCorePoolSize : Math.max(actualParallelism, actualCorePoolSize));
		Integer actualMaxPoolSize = ((this.getMaxPoolSize() == null) || (this.getMaxPoolSize() < 0)
				? (this.getMaxPoolSizeCpuMultiplier() == null ? Integer.MAX_VALUE
						: ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * this.getMaxPoolSizeCpuMultiplier())).intValue())
				: this.getMaxPoolSize());
		actualMaxPoolSize = (Math.max(actualCorePoolSize, actualMaxPoolSize));
		final Long actualKeepAliveMillis = this.getKeepAlive().toMillis();
		final TimeUnit actualKeepAliveUnit = TimeUnit.MILLISECONDS;
		final ThreadFactory factory = (this.getVirtual() ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory());
		final DynamicThreadPoolFactory actualFactory = new DynamicThreadPoolFactory(this).withParallelism(actualParallelism).withMinRunnable(actualMinRunnable)
				.withCorePoolSize(actualCorePoolSize).withMaxPoolSize(actualMaxPoolSize);
		DynamicThreadPoolFactory.LOGGER.info("Thread pool '" + this.getName() + "' created: '" + ToStringBuilder.reflectionToString(actualFactory) + "'.");

		// If parallelism is set.
		if (actualParallelism != null) {
			final ForkJoinPool forkJoinPool = new ForkJoinPool(actualParallelism,
					new ConfigurableForkJoinWorkerThreadFactory(ForkJoinPool.defaultForkJoinWorkerThreadFactory, this.getName(), this.getPriority()), null,
					true, actualCorePoolSize, actualMaxPoolSize, actualMinRunnable, null, actualKeepAliveMillis, actualKeepAliveUnit);
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
			final BlockingQueue<Runnable> queue = ((actualMaxPoolSize == null) || (actualMaxPoolSize == Integer.MAX_VALUE)
					? new SynchronousQueue<>(this.getFairQueue())
					: new LinkedBlockingQueue<>(this.getMaxQueueSize()));
			final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(actualCorePoolSize, actualMaxPoolSize, actualKeepAliveMillis,
					actualKeepAliveUnit, queue, new ConfigurableThreadFactory(factory, this.getName(), this.getPriority()));
			executor = threadPoolExecutor;
		}

		// Returns the executor.
		return executor;
	}

}
