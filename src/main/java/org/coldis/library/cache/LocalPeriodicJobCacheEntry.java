package org.coldis.library.cache;

import java.time.LocalDateTime;

/**
 * Local periodic job cache entry.
 *
 * @deprecated Use {@link LocalThrottleEntry}.
 */
@Deprecated
public class LocalPeriodicJobCacheEntry extends LocalThrottleEntry {

	/** Creates a new instance of the class. */
	public LocalPeriodicJobCacheEntry() {
		super();
	}

	/**
	 * Creates a new instance of the class.
	 *
	 * @param lastRunAt    Last run at.
	 * @param nextRunAfter Next run after.
	 */
	public LocalPeriodicJobCacheEntry(final LocalDateTime lastRunAt, final LocalDateTime nextRunAfter) {
		super(lastRunAt, nextRunAfter);
	}

	/**
	 * Creates a copy of an entry.
	 *
	 * @param entry Entry.
	 */
	public LocalPeriodicJobCacheEntry(final LocalThrottleEntry entry) {
		super(entry);
	}

}
