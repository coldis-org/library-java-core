package org.coldis.library.cache;

import java.time.LocalDateTime;

/**
 * A run acquired from a {@link LocalThrottle}, which
 * {@link LocalThrottle#release(LocalThrottleReservation)} gives back when the
 * run did not happen. Only the throttle creates reservations, so a caller cannot
 * build one that resets a key to an arbitrary earlier state.
 */
public final class LocalThrottleReservation {

	/** Key. */
	private final String key;

	/** When the run was acquired (the key's last run at). */
	private final LocalDateTime acquiredAt;

	/** When the key may run again. */
	private final LocalDateTime nextRunAfter;

	/** The key's last run at before this run (null when it had never run). */
	private final LocalDateTime previousLastRunAt;

	/** The key's next run after before this run (null when it had never run). */
	private final LocalDateTime previousNextRunAfter;

	/**
	 * Creates a reservation (only the throttle does).
	 *
	 * @param key                  Key.
	 * @param acquiredAt           When the run was acquired.
	 * @param nextRunAfter         When the key may run again.
	 * @param previousLastRunAt    The key's last run at before this run.
	 * @param previousNextRunAfter The key's next run after before this run.
	 */
	LocalThrottleReservation(
			final String key,
			final LocalDateTime acquiredAt,
			final LocalDateTime nextRunAfter,
			final LocalDateTime previousLastRunAt,
			final LocalDateTime previousNextRunAfter) {
		this.key = key;
		this.acquiredAt = acquiredAt;
		this.nextRunAfter = nextRunAfter;
		this.previousLastRunAt = previousLastRunAt;
		this.previousNextRunAfter = previousNextRunAfter;
	}

	/**
	 * Gets the key.
	 *
	 * @return The key.
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * Gets when the run was acquired.
	 *
	 * @return When the run was acquired.
	 */
	public LocalDateTime getAcquiredAt() {
		return this.acquiredAt;
	}

	/**
	 * Gets when the key may run again.
	 *
	 * @return When the key may run again.
	 */
	public LocalDateTime getNextRunAfter() {
		return this.nextRunAfter;
	}

	/**
	 * Gets the key's last run at before this run.
	 *
	 * @return The key's last run at before this run (null when it had never run).
	 */
	LocalDateTime getPreviousLastRunAt() {
		return this.previousLastRunAt;
	}

	/**
	 * Gets the key's next run after before this run.
	 *
	 * @return The key's next run after before this run (null when it had never
	 *         run).
	 */
	LocalDateTime getPreviousNextRunAfter() {
		return this.previousNextRunAfter;
	}

}
