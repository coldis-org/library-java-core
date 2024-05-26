package org.coldis.library.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple thread factory.
 */
public class SimpleThreadFactory implements ThreadFactory {

	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;
	private final Boolean daemon;
	private final Integer priority;
	private final Boolean virtual;

	/**
	 * Constructor.
	 */
	public SimpleThreadFactory(final String namePrefix, final Boolean daemon, final Integer priority, final Boolean virtual) {
		@SuppressWarnings("removal")
		final SecurityManager securityManager = System.getSecurityManager();
		this.group = ((securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
		this.namePrefix = namePrefix;
		this.daemon = daemon;
		this.priority = priority;
		this.virtual = virtual;
	}

	/**
	 * Calculates the thread pool size.
	 *
	 * @param  size              Size.
	 * @param  sizeCpuMultiplier Size CPU multiplier.
	 * @return                   Thread pool size.
	 */
	public static Integer calculateThreadPoolSize(
			final Integer size,
			final Double sizeCpuMultiplier) {
		return ((size == null) || (size < 0) ? ((Double) (((Integer) Runtime.getRuntime().availableProcessors()).doubleValue() * sizeCpuMultiplier)).intValue()
				: size);
	}

	/*
	 * Gets a new thread.
	 */
	@Override
	public Thread newThread(
			final Runnable runnable) {
		Thread thread = null;
		final String name = (this.namePrefix + (this.virtual ? "-virtual" : "-platform") + "-" + this.threadNumber.getAndIncrement());
		if (this.virtual) {
			final ThreadFactory threadFactory = Thread.ofVirtual().factory();
			thread = threadFactory.newThread(runnable);
			thread.setName(name);
		}
		else {
			thread = new Thread(this.group, runnable, name, 0);
			thread.setDaemon(this.daemon);
		}
		thread.setPriority(this.priority);
		return thread;
	}
}
