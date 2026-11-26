package org.coldis.library.cache;

import java.time.LocalDateTime;
import java.util.Objects;

/** Local throttle entry: when a key last ran, and when it may run again. */
public class LocalThrottleEntry {

	/** Last run at. */
	private LocalDateTime lastRunAt;

	/** Next run after. */
	private LocalDateTime nextRunAfter;

	/**
	 * The reservation of the current run, which only that reservation can release
	 * (not copied, and not part of the equality).
	 */
	private LocalThrottleReservation reservation;

	/** Creates a new instance of the class. */
	public LocalThrottleEntry() {
		super();
	}

	/**
	 * Creates a new instance of the class.
	 *
	 * @param lastRunAt    Last run at.
	 * @param nextRunAfter Next run after.
	 */
	public LocalThrottleEntry(final LocalDateTime lastRunAt, final LocalDateTime nextRunAfter) {
		this();
		this.lastRunAt = lastRunAt;
		this.nextRunAfter = nextRunAfter;
	}

	/**
	 * Creates a copy of an entry.
	 *
	 * @param entry Entry.
	 */
	public LocalThrottleEntry(final LocalThrottleEntry entry) {
		// The stored values, so a key that never ran stays new (the getters return LocalDateTime.MIN).
		this(entry.getStoredLastRunAt(), entry.getStoredNextRunAfter());
	}

	/**
	 * Gets the last run at.
	 *
	 * @return The last run at.
	 */
	public LocalDateTime getLastRunAt() {
		return (this.lastRunAt == null ? LocalDateTime.MIN : this.lastRunAt);
	}

	/**
	 * Sets the last run at.
	 *
	 * @param lastRunAt New last run at.
	 */
	public void setLastRunAt(
			final LocalDateTime lastRunAt) {
		this.lastRunAt = lastRunAt;
	}

	/**
	 * Gets the next run after.
	 *
	 * @return The next run after.
	 */
	public LocalDateTime getNextRunAfter() {
		return (this.nextRunAfter == null ? LocalDateTime.MIN : this.nextRunAfter);
	}

	/**
	 * Sets the next run after.
	 *
	 * @param nextRunAfter New next run after.
	 */
	public void setNextRunAfter(
			final LocalDateTime nextRunAfter) {
		this.nextRunAfter = nextRunAfter;
	}

	/**
	 * Gets the reservation of the current run.
	 *
	 * @return The reservation of the current run (null when released or never
	 *         acquired).
	 */
	LocalThrottleReservation getReservation() {
		return this.reservation;
	}

	/**
	 * Sets the reservation of the current run.
	 *
	 * @param reservation The reservation of the current run.
	 */
	void setReservation(
			final LocalThrottleReservation reservation) {
		this.reservation = reservation;
	}

	/**
	 * Gets the last run at as stored (null when the key never ran).
	 *
	 * @return The last run at as stored.
	 */
	LocalDateTime getStoredLastRunAt() {
		return this.lastRunAt;
	}

	/**
	 * Gets the next run after as stored (null when the key never ran).
	 *
	 * @return The next run after as stored.
	 */
	LocalDateTime getStoredNextRunAfter() {
		return this.nextRunAfter;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.lastRunAt, this.nextRunAfter);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(
			final Object obj) {
		boolean equal = (this == obj);
		if (!equal && (obj != null) && (this.getClass() == obj.getClass())) {
			final LocalThrottleEntry other = (LocalThrottleEntry) obj;
			equal = Objects.equals(this.lastRunAt, other.lastRunAt) && Objects.equals(this.nextRunAfter, other.nextRunAfter);
		}
		return equal;
	}

}
