package org.coldis.library.cache;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.helper.LockHelper;

/** Helper for jobs running periodically. */
public class LocalPeriodicJobCache {

	/** Last job runs. */
	private final Map<String, LocalPeriodicJobCacheEntry> lastJobRuns = new ConcurrentHashMap<>();

	/** Cache lock. */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	/** Lob id. */
	private final String id;

	/**
	 * Creates a new instance of the class.
	 *
	 * @param id Lob id.
	 */
	public LocalPeriodicJobCache(final String id) {
		super();
		this.id = id;
	}

	/**
	 * Creates a new instance of the class.
	 */
	public LocalPeriodicJobCache() {
		this(UUID.randomUUID().toString());
	}

	/**
	 * Gets the lock key for a given key.
	 *
	 * @param  key Key.
	 * @return     Lock key.
	 */
	private Object getKeyLock(
			final String key) {
		return LockHelper.getStringLockKey(LocalPeriodicJobCache.class.getSimpleName() + "-" + this.id + "-" + key);
	}

	/**
	 * Gets the last run for a key.
	 *
	 * @param  key Job key.
	 * @return     Last run.
	 */
	public LocalPeriodicJobCacheEntry getLastRun(
			final String key) {
		try {
			// Gets the read lock.
			this.lock.readLock().lock();
			synchronized (this.getKeyLock(key)) {
				return new LocalPeriodicJobCacheEntry(this.lastJobRuns.getOrDefault(key, new LocalPeriodicJobCacheEntry()));
			}
		}
		// Releases the read lock.
		finally {
			this.lock.readLock().unlock();
		}
	}

	/**
	 * Clears the last run for a kind.
	 *
	 * @param kind Job kind.
	 */
	public void clear() {
		try {
			this.lock.writeLock().lock();
			this.lastJobRuns.clear();
		}
		finally {
			this.lock.writeLock().unlock();
		}
	}

	/**
	 * Clears expired runs.
	 */
	public void clearExpired() {
		try {
			this.lock.writeLock().lock();

			final LocalDateTime now = DateTimeHelper.getCurrentLocalDateTime();
			for (final String key : this.lastJobRuns.keySet()) {
				final LocalPeriodicJobCacheEntry lastRun = this.lastJobRuns.get(key);
				if (now.isAfter(lastRun.getNextRunAfter())) {
					this.lastJobRuns.remove(key);
				}
			}
		}
		finally {
			this.lock.writeLock().unlock();
		}
	}

	/**
	 * If a job should run.
	 *
	 * @param kind       Job kind.
	 * @param key        Job key.
	 * @param waitPeriod Wait period.
	 */
	public Boolean shouldRun(
			final String key,
			final Duration waitPeriod,
			final TemporalUnit maximumWaitUnit) {
		try {
			// Gets the read lock.
			this.lock.readLock().lock();

			final LocalDateTime now = DateTimeHelper.getCurrentLocalDateTime();
			final LocalDateTime expiration = DateTimeHelper.maximum(now.minus(waitPeriod), (maximumWaitUnit == null ? null : now.truncatedTo(maximumWaitUnit)));

			// Tries to get the time for the checked key.
			boolean shouldRun = false;
			synchronized (this.getKeyLock(key)) {
				final LocalPeriodicJobCacheEntry lastRun = this.lastJobRuns.computeIfAbsent(key, k -> new LocalPeriodicJobCacheEntry());

				// Should run if the last run was not found or is before the expiration.
				shouldRun = (lastRun.getLastRunAt().isBefore(expiration));

				// If should run, updates the last run.
				if (shouldRun) {
					lastRun.setLastRunAt(now);
					lastRun.setNextRunAfter(DateTimeHelper.mininum(now.plus(waitPeriod),
							(maximumWaitUnit == null ? null : maximumWaitUnit.addTo(now.truncatedTo(maximumWaitUnit), 1))));
				}
			}

			// Returns if the job should run.
			return shouldRun;
		}
		// Releases the read lock.
		finally {
			this.lock.readLock().unlock();
		}
	}
	

}
