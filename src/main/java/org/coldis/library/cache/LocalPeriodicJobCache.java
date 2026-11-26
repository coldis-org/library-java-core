package org.coldis.library.cache;

/**
 * Helper for jobs running periodically.
 *
 * @deprecated Use {@link LocalThrottle}.
 */
@Deprecated
public class LocalPeriodicJobCache extends LocalThrottle {

	/**
	 * Creates a new instance of the class.
	 *
	 * @param timesToRunBeforeClearingExpired Number of times to run before clearing
	 *                                            expired.
	 */
	public LocalPeriodicJobCache(final Long timesToRunBeforeClearingExpired) {
		super(timesToRunBeforeClearingExpired);
	}

	/** Creates a new instance of the class. */
	public LocalPeriodicJobCache() {
		super();
	}

	/**
	 * @see org.coldis.library.cache.LocalThrottle#getLastRun(java.lang.String)
	 */
	@Override
	public LocalPeriodicJobCacheEntry getLastRun(
			final String key) {
		return new LocalPeriodicJobCacheEntry(super.getLastRun(key));
	}

}
