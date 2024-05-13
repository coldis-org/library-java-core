package org.coldis.library.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Pooled thread factory.
 */
public class PooledThreadFactory implements ThreadFactory {

	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;
	private final Boolean useVirtualThreads;
	private final Integer priority;

	/**
	 * Constructor.
	 */
	PooledThreadFactory(final Integer priority, final Boolean useVirtualThreads) {
		@SuppressWarnings("removal")
		final SecurityManager securityManager = System.getSecurityManager();
		this.group = ((securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
		this.namePrefix = "pool-log-thread-";
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
			thread = Thread.ofVirtual().unstarted(runnable);
			thread.setName(this.namePrefix + this.threadNumber.getAndIncrement());
		}
		else {
			thread = new Thread(this.group, runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
		}
		thread.setDaemon(false);
		thread.setPriority(priority);
		return thread;
	}
}
