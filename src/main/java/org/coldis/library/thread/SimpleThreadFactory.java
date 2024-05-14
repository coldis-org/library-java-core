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
	private final Boolean useVirtualThreads;

	/**
	 * Constructor.
	 */
	public SimpleThreadFactory(final String namePrefix, final Boolean daemon, final Integer priority, final Boolean useVirtualThreads) {
		@SuppressWarnings("removal")
		final SecurityManager securityManager = System.getSecurityManager();
		this.group = ((securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
		this.namePrefix = namePrefix;
		this.daemon = daemon;
		this.priority = priority;
		this.useVirtualThreads = useVirtualThreads;
	}

	/*
	 * Gets a new thread.
	 */
	@Override
	public Thread newThread(
			final Runnable runnable) {
		Thread thread = null;
		if (this.useVirtualThreads) {
			final ThreadFactory threadFactory = Thread.ofVirtual().factory();
			thread = threadFactory.newThread(runnable);
			thread.setName(this.namePrefix + this.threadNumber.getAndIncrement());
		}
		else {
			thread = new Thread(this.group, runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
			thread.setDaemon(this.daemon);
		}
		thread.setPriority(this.priority);
		return thread;
	}
}
