package org.coldis.library.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pooled executor.
 */
public class PooledExecutor implements Executor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PooledExecutor.class);

	/**
	 * Executor.
	 */
	private Executor executor;

	/**
	 * Sets the thread pool size.
	 *
	 * @param parallelism  Parallelism (activates work stealing pool).
	 * @param corePoolSize Core pool size (activates blocking thread pool).
	 * @param maxPoolSize  Max pool size.
	 * @param queueSize    Queue size.
	 * @param keepAlive    Keep alive.
	 */
	public PooledExecutor(
			final Boolean useVirtualThreads,
			final Integer corePoolSize,
			final Integer maxPoolSize,
			final Double maxPoolSizeCpuMultiplier,
			final Integer queueSize,
			final Integer keepAlive) {
		super();
		if ((corePoolSize != null) && (corePoolSize > 0)) {
			final Integer actualMaxPoolSize = ((maxPoolSize == null) || (maxPoolSize < 0)
					? ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * maxPoolSizeCpuMultiplier)).intValue()
					: maxPoolSize);
			PooledExecutor.LOGGER.info("History max thread pool size is: " + actualMaxPoolSize);
			final ThreadFactory factory = new PooledThreadFactory("pool-historical-entity-thread", true, Thread.MIN_PRIORITY + 1, useVirtualThreads);
			final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, actualMaxPoolSize, keepAlive, TimeUnit.SECONDS,
					new ArrayBlockingQueue<>(queueSize), factory);
			threadPoolExecutor.allowCoreThreadTimeOut(true);
			this.executor = threadPoolExecutor;
		}
		else {
			this.executor = (useVirtualThreads ? Executors.newVirtualThreadPerTaskExecutor() : Executors.newSingleThreadExecutor());
		}
	}

	/**
	 * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
	 */
	@Override
	public void execute(
			final Runnable command) {
		this.executor.execute(command);
	}

}
