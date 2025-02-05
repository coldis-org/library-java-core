package org.coldis.library.helper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/** Helper for jobs running periodically. */
public class LocalPeriodicJobHelper {

	/** Last job runs. */
	public static final Map<String, Map<String, Map.Entry<LocalDateTime, LocalDateTime>>> LAST_JOB_RUNS = new ConcurrentHashMap<>();

	/**
	 * Clears the last run for a kind.
	 *
	 * @param kind Job kind.
	 */
	public static void clear(
			final String kind) {
		synchronized (LockHelper.getStringLockKey(kind)) {
			LocalPeriodicJobHelper.LAST_JOB_RUNS.remove(kind);
		}
	}

	/**
	 * Clears expired runs.
	 */
	public static void clearExpired() {
		final LocalDateTime now = DateTimeHelper.getCurrentLocalDateTime();
		for (final String kind : LocalPeriodicJobHelper.LAST_JOB_RUNS.keySet()) {
			for (final String value : LocalPeriodicJobHelper.LAST_JOB_RUNS.get(kind).keySet()) {
				synchronized (LockHelper.getStringLockKey(kind + "-" + value)) {
					final Entry<LocalDateTime, LocalDateTime> lastRun = LocalPeriodicJobHelper.LAST_JOB_RUNS.get(kind).get(value);
					if ((lastRun.getKey() == null) || (lastRun.getValue() == null) || now.isAfter(lastRun.getValue())) {
						LocalPeriodicJobHelper.LAST_JOB_RUNS.get(kind).remove(value);
					}
				}
			}
		}
	}

	/**
	 * If a job should run.
	 *
	 * @param kind       Job kind.
	 * @param value      Job value.
	 * @param waitPeriod Wait period.
	 */
	public static Boolean shouldRun(
			final String kind,
			final String value,
			final Duration waitPeriod,
			final TemporalUnit maximumWaitUnit) {
		final LocalDateTime now = DateTimeHelper.getCurrentLocalDateTime();
		final LocalDateTime expiration = DateTimeHelper.maximum(now.minus(waitPeriod), (maximumWaitUnit == null ? null : now.truncatedTo(maximumWaitUnit)));

		// Makes sure the entry for the kind exists.
		synchronized (LockHelper.getStringLockKey(kind)) {
			LocalPeriodicJobHelper.LAST_JOB_RUNS.put(kind, LocalPeriodicJobHelper.LAST_JOB_RUNS.getOrDefault(kind, new ConcurrentHashMap<>()));
		}
		final Map<String, Map.Entry<LocalDateTime, LocalDateTime>> kindChecks = LocalPeriodicJobHelper.LAST_JOB_RUNS.get(kind);

		// Tries to get the time for the checked value.
		synchronized (LockHelper.getStringLockKey(kind + "-" + value)) {
			kindChecks.put(value, kindChecks.getOrDefault(value, Map.entry(LocalDateTime.MIN, LocalDateTime.MIN)));
		}
		final Entry<LocalDateTime, LocalDateTime> lastRunAt = kindChecks.get(value);

		// Should run if the last run was not found or is before the expiration.
		final boolean shouldRun = ((lastRunAt == null) || (lastRunAt.getKey() == null) || (lastRunAt.getKey().isBefore(expiration)));

		// If should run, updates the last run.
		if (shouldRun) {
			synchronized (LockHelper.getStringLockKey(kind + "-" + value)) {
				kindChecks.put(value, Map.entry(now, DateTimeHelper.mininum(now.plus(waitPeriod),
						(maximumWaitUnit == null ? null : maximumWaitUnit.addTo(now.truncatedTo(maximumWaitUnit), 1)))));
			}
		}

		// Returns if the job should run.
		return shouldRun;
	}

}
