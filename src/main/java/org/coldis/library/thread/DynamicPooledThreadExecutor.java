package org.coldis.library.thread;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pooled executor.
 */
public class DynamicPooledThreadExecutor implements ScheduledExecutorService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicPooledThreadExecutor.class);

	/**
	 * Executor.
	 */
	private ExecutorService executor;

	/**
	 * Sets the thread pool size.
	 *
	 * @param parallelism      Parallelism (activates work stealing pool).
	 * @param corePoolSize     Core pool size (activates blocking thread pool).
	 * @param maxPoolSize      Max pool size.
	 * @param queueSize        Queue size.
	 * @param keepAliveSeconds Keep alive.
	 */
	public DynamicPooledThreadExecutor(
			final String name,
			final Integer priority,
			final Boolean daemon,
			final Boolean virtual,
			final Boolean scheduled,
			final Integer corePoolSize,
			final Double corePoolSizeCpuMultiplier,
			final Integer maxPoolSize,
			final Double maxPoolSizeCpuMultiplier,
			final Integer queueSize,
			final Duration keepAlive) {
		super();
		final String actualName = (name + (scheduled ? "-scheduled" : ""));
		if (((corePoolSize != null) && (corePoolSize > 0)) || ((corePoolSizeCpuMultiplier != null) && (corePoolSizeCpuMultiplier > 0))) {
			final Integer actualCorePoolSize = ((corePoolSize == null) || (corePoolSize < 0)
					? ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * corePoolSizeCpuMultiplier)).intValue()
					: corePoolSize);
			final Integer actualMaxPoolSize = ((maxPoolSize == null) || (maxPoolSize < 0)
					? ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * maxPoolSizeCpuMultiplier)).intValue()
					: maxPoolSize);
			DynamicPooledThreadExecutor.LOGGER
					.info("Thread pool '" + actualName + "' created with core size '" + actualCorePoolSize + "' and max size '" + actualMaxPoolSize + "'.");
			final ThreadFactory factory = new SimpleThreadFactory(actualName, daemon, priority, virtual);
			ThreadPoolExecutor threadPoolExecutor = null;
			if (scheduled) {
				threadPoolExecutor = new ScheduledThreadPoolExecutor(actualCorePoolSize, factory);
			}
			else {
				final BlockingQueue<Runnable> queue = ((queueSize == null) || (queueSize <= 0) ? new SynchronousQueue<>(true)
						: new ArrayBlockingQueue<>(queueSize, true));
				threadPoolExecutor = new ThreadPoolExecutor(actualCorePoolSize, actualMaxPoolSize, keepAlive.toMillis(), TimeUnit.MILLISECONDS, queue, factory);
			}
			threadPoolExecutor.allowCoreThreadTimeOut(true);
			this.executor = threadPoolExecutor;
		}
		else {
			if (scheduled) {
				this.executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(),
						new SimpleThreadFactory(actualName, daemon, priority, virtual));
			}
			else {
				this.executor = (virtual ? Executors.newVirtualThreadPerTaskExecutor() : Executors.newCachedThreadPool());
			}
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

	/**
	 * @see java.util.concurrent.ExecutorService#shutdown()
	 */
	@Override
	public void shutdown() {
		this.executor.shutdown();
	}

	/**
	 * @see java.util.concurrent.ExecutorService#shutdownNow()
	 */
	@Override
	public List<Runnable> shutdownNow() {
		return this.executor.shutdownNow();
	}

	/**
	 * @see java.util.concurrent.ExecutorService#isShutdown()
	 */
	@Override
	public boolean isShutdown() {
		return this.executor.isShutdown();
	}

	/**
	 * @see java.util.concurrent.ExecutorService#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		return this.executor.isTerminated();
	}

	/**
	 * @see java.util.concurrent.ExecutorService#awaitTermination(long,
	 *      java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean awaitTermination(
			final long timeout,
			final TimeUnit unit) throws InterruptedException {
		return this.executor.awaitTermination(timeout, unit);
	}

	/**
	 * @see java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
	 */
	@Override
	public <T> Future<T> submit(
			final Callable<T> task) {
		return this.executor.submit(task);
	}

	/**
	 * @see java.util.concurrent.ExecutorService#submit(java.lang.Runnable,
	 *      java.lang.Object)
	 */
	@Override
	public <T> Future<T> submit(
			final Runnable task,
			final T result) {
		return this.executor.submit(task, result);
	}

	/**
	 * @see java.util.concurrent.ExecutorService#submit(java.lang.Runnable)
	 */
	@Override
	public Future<?> submit(
			final Runnable task) {
		return this.executor.submit(task);
	}

	/**
	 * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection)
	 */
	@Override
	public <T> List<Future<T>> invokeAll(
			final Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return this.executor.invokeAll(tasks);
	}

	/**
	 * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection,
	 *      long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public <T> List<Future<T>> invokeAll(
			final Collection<? extends Callable<T>> tasks,
			final long timeout,
			final TimeUnit unit) throws InterruptedException {
		return this.executor.invokeAll(tasks, timeout, unit);
	}

	/**
	 * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection)
	 */
	@Override
	public <T> T invokeAny(
			final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return this.executor.invokeAny(tasks);
	}

	/**
	 * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection,
	 *      long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public <T> T invokeAny(
			final Collection<? extends Callable<T>> tasks,
			final long timeout,
			final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.executor.invokeAny(tasks, timeout, unit);
	}

	/**
	 * @see java.util.concurrent.ScheduledExecutorService#schedule(java.lang.Runnable,
	 *      long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public ScheduledFuture<?> schedule(
			final Runnable command,
			final long delay,
			final TimeUnit unit) {
		if (this.executor instanceof ScheduledExecutorService) {
			return ((ScheduledExecutorService) this.executor).schedule(command, delay, unit);
		}
		else {
			throw new UnsupportedOperationException("Executor is not a scheduled executor.");
		}
	}

	/**
	 * @see java.util.concurrent.ScheduledExecutorService#schedule(java.util.concurrent.Callable,
	 *      long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public <V> ScheduledFuture<V> schedule(
			final Callable<V> callable,
			final long delay,
			final TimeUnit unit) {
		if (this.executor instanceof ScheduledExecutorService) {
			return ((ScheduledExecutorService) this.executor).schedule(callable, delay, unit);
		}
		else {
			throw new UnsupportedOperationException("Executor is not a scheduled executor.");
		}
	}

	/**
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable,
	 *      long, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(
			final Runnable command,
			final long initialDelay,
			final long period,
			final TimeUnit unit) {
		if (this.executor instanceof ScheduledExecutorService) {
			return ((ScheduledExecutorService) this.executor).scheduleAtFixedRate(command, initialDelay, period, unit);
		}
		else {
			throw new UnsupportedOperationException("Executor is not a scheduled executor.");
		}
	}

	/**
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable,
	 *      long, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(
			final Runnable command,
			final long initialDelay,
			final long delay,
			final TimeUnit unit) {
		if (this.executor instanceof ScheduledExecutorService) {
			return ((ScheduledExecutorService) this.executor).scheduleWithFixedDelay(command, initialDelay, delay, unit);
		}
		else {
			throw new UnsupportedOperationException("Executor is not a scheduled executor.");
		}
	}

}
