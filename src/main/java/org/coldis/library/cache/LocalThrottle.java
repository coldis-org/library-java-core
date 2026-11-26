package org.coldis.library.cache;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import org.coldis.library.helper.DateTimeHelper;

/**
 * Local (per instance) throttle: lets a key run at most once per wait period.
 * A run is acquired when it is allowed, so concurrent callers of the same key
 * never both run; a run that did not happen can be released, giving the key
 * back.
 */
public class LocalThrottle {

	/** Default number of runs before clearing the expired entries. */
	public static final Long DEFAULT_TIMES_TO_RUN_BEFORE_CLEARING_EXPIRED = 10000L;

	/** Clear executor. */
	private final ExecutorService CLEAR_EXECUTOR = Executors.newSingleThreadExecutor();

	/** Last runs, by key. */
	private final Map<String, LocalThrottleEntry> lastRuns = new ConcurrentHashMap<>();

	/** Lock (read for runs, write for clearing). */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	/** Number of runs before clearing the expired entries. */
	private final Long timesToRunBeforeClearingExpired;

	/** Current date and time. */
	private final Supplier<LocalDateTime> currentDateTime;

	/** Number of runs tried since the expired entries were last cleared. */
	private final AtomicLong tryRunCount = new AtomicLong();

	/**
	 * Creates a new instance of the class.
	 *
	 * @param timesToRunBeforeClearingExpired Number of runs before clearing the
	 *                                            expired entries.
	 * @param currentDateTime                 Current date and time (e.g. the
	 *                                            wall clock, when the
	 *                                            application clock may be moved,
	 *                                            as in tests).
	 */
	public LocalThrottle(final Long timesToRunBeforeClearingExpired, final Supplier<LocalDateTime> currentDateTime) {
		super();
		this.timesToRunBeforeClearingExpired = timesToRunBeforeClearingExpired;
		this.currentDateTime = Objects.requireNonNull(currentDateTime);
	}

	/**
	 * Creates a new instance of the class, on the application clock
	 * ({@link DateTimeHelper}).
	 *
	 * @param timesToRunBeforeClearingExpired Number of runs before clearing the
	 *                                            expired entries.
	 */
	public LocalThrottle(final Long timesToRunBeforeClearingExpired) {
		this(timesToRunBeforeClearingExpired, DateTimeHelper::getCurrentLocalDateTime);
	}

	/** Creates a new instance of the class, on the application clock. */
	public LocalThrottle() {
		this(LocalThrottle.DEFAULT_TIMES_TO_RUN_BEFORE_CLEARING_EXPIRED);
	}

	/**
	 * Gets (a copy of) the last run of a key.
	 *
	 * @param  key Key.
	 * @return     The last run.
	 */
	public LocalThrottleEntry getLastRun(
			final String key) {
		try {
			this.lock.readLock().lock();
			synchronized (this.lastRuns.computeIfAbsent(key, newKey -> new LocalThrottleEntry())) {
				return new LocalThrottleEntry(this.lastRuns.get(key));
			}
		}
		finally {
			this.lock.readLock().unlock();
		}
	}

	/** Clears every entry. */
	public void clear() {
		try {
			this.lock.writeLock().lock();
			this.lastRuns.clear();
		}
		finally {
			this.lock.writeLock().unlock();
		}
	}

	/** Clears the expired entries. */
	public void clearExpired() {
		try {
			this.lock.writeLock().lock();
			final LocalDateTime now = this.currentDateTime.get();
			this.lastRuns.values().removeIf(lastRun -> now.isAfter(lastRun.getNextRunAfter()));
		}
		finally {
			this.tryRunCount.set(0L);
			this.lock.writeLock().unlock();
		}
	}

	/**
	 * Clears the expired entries (in the background) every so many runs: once per
	 * crossing of the threshold, not on every run until the clear resets the
	 * counter.
	 */
	private void clearExpiredIfDue() {
		if (this.tryRunCount.incrementAndGet() == this.timesToRunBeforeClearingExpired) {
			this.CLEAR_EXECUTOR.execute(this::clearExpired);
		}
	}

	/**
	 * Acquires a run of a key: when the key did not run within the wait period
	 * (and, with a maximum wait unit, within the current unit), records the run
	 * and returns it; otherwise returns null.
	 *
	 * @param  key             Key.
	 * @param  waitPeriod      Wait period.
	 * @param  maximumWaitUnit Maximum wait unit (the key may run again once the
	 *                             unit turns, e.g. once a day); optional.
	 * @return                 The run acquired, or null when the key must wait.
	 */
	public LocalThrottleReservation tryAcquire(
			final String key,
			final Duration waitPeriod,
			final TemporalUnit maximumWaitUnit) {
		LocalThrottleReservation reservation = null;
		try {
			this.lock.readLock().lock();
			final LocalDateTime now = this.currentDateTime.get();
			final LocalDateTime expiration = DateTimeHelper.maximum(now.minus(waitPeriod),
					(maximumWaitUnit == null ? null : now.truncatedTo(maximumWaitUnit)));
			synchronized (this.lastRuns.computeIfAbsent(key, newKey -> new LocalThrottleEntry())) {
				final LocalThrottleEntry lastRun = this.lastRuns.get(key);
				// Runs if the key did not run since the expiration.
				if (lastRun.getLastRunAt().isBefore(expiration)) {
					final LocalDateTime nextRunAfter = DateTimeHelper.mininum(now.plus(waitPeriod),
							(maximumWaitUnit == null ? null : maximumWaitUnit.addTo(now.truncatedTo(maximumWaitUnit), 1)));
					reservation = new LocalThrottleReservation(key, now, nextRunAfter, lastRun.getStoredLastRunAt(), lastRun.getStoredNextRunAfter());
					lastRun.setLastRunAt(now);
					lastRun.setNextRunAfter(nextRunAfter);
				}
			}
		}
		finally {
			this.lock.readLock().unlock();
			this.clearExpiredIfDue();
		}
		return reservation;
	}

	/**
	 * Releases a run that did not happen, giving the key back to the state it had
	 * before. Does nothing when a later run of the key was acquired since (or the
	 * entry was cleared). Only release a run whose action certainly did not reach
	 * anything outside the system (e.g. after its transaction rolled back), never
	 * after a failure that may have happened after the action (such as a timeout
	 * from an outside provider): the key would run again, repeating the action.
	 *
	 * @param reservation The run acquired.
	 */
	public void release(
			final LocalThrottleReservation reservation) {
		if (reservation != null) {
			try {
				this.lock.readLock().lock();
				final LocalThrottleEntry lastRun = this.lastRuns.get(reservation.getKey());
				if (lastRun != null) {
					synchronized (lastRun) {
						if (Objects.equals(lastRun.getStoredLastRunAt(), reservation.getAcquiredAt())
								&& Objects.equals(lastRun.getStoredNextRunAfter(), reservation.getNextRunAfter())) {
							lastRun.setLastRunAt(reservation.getPreviousLastRunAt());
							lastRun.setNextRunAfter(reservation.getPreviousNextRunAfter());
						}
					}
				}
			}
			finally {
				this.lock.readLock().unlock();
			}
		}
	}

	/**
	 * Checks if a key should run, recording the run when it should (see
	 * {@link #tryAcquire(String, Duration, TemporalUnit)}).
	 *
	 * @param  key             Key.
	 * @param  waitPeriod      Wait period.
	 * @param  maximumWaitUnit Maximum wait unit; optional.
	 * @return                 If the key should run.
	 */
	public Boolean shouldRun(
			final String key,
			final Duration waitPeriod,
			final TemporalUnit maximumWaitUnit) {
		return (this.tryAcquire(key, waitPeriod, maximumWaitUnit) != null);
	}

	/**
	 * Runs a job if it should run.
	 *
	 * @param job             Job to run.
	 * @param key             Job key.
	 * @param waitPeriod      Wait period.
	 * @param maximumWaitUnit Maximum wait unit; optional.
	 */
	public void run(
			final Runnable job,
			final String key,
			final Duration waitPeriod,
			final TemporalUnit maximumWaitUnit) {
		if (this.shouldRun(key, waitPeriod, maximumWaitUnit)) {
			job.run();
		}
	}

}
