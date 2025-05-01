package org.coldis.library.cache;

import java.time.LocalDateTime;
import java.util.Objects;

/** Local periodic job cache entry. */
public class LocalPeriodicJobCacheEntry {

	/** Last run at. */
	private LocalDateTime lastRunAt;

	/** Run run after. */
	private LocalDateTime nextRunAfter;

	/**
	 * Creates a new instance of the class.
	 */
	public LocalPeriodicJobCacheEntry() {
		super();
	}

	/**
	 * Creates a new instance of the class.
	 */
	public LocalPeriodicJobCacheEntry(final LocalDateTime lastRunAt, final LocalDateTime nextRunAfter) {
		this();
		this.lastRunAt = lastRunAt;
		this.nextRunAfter = nextRunAfter;
	}

	/**
	 * Creates a new instance of the class.
	 */
	public LocalPeriodicJobCacheEntry(final LocalPeriodicJobCacheEntry periodicJobCacheEntry) {
		this(periodicJobCacheEntry.lastRunAt, periodicJobCacheEntry.nextRunAfter);
	}

	/**
	 * Gets the lastRunAt.
	 *
	 * @return The lastRunAt.
	 */
	public LocalDateTime getLastRunAt() {
		return (this.lastRunAt == null ? LocalDateTime.MIN : this.lastRunAt);
	}

	/**
	 * Sets the lastRunAt.
	 *
	 * @param lastRunAt New lastRunAt.
	 */
	public void setLastRunAt(
			final LocalDateTime lastRunAt) {
		this.lastRunAt = lastRunAt;
	}

	/**
	 * Gets the nextRunAfter.
	 *
	 * @return The nextRunAfter.
	 */
	public LocalDateTime getNextRunAfter() {
		return (this.nextRunAfter == null ? LocalDateTime.MIN : this.nextRunAfter);
	}

	/**
	 * Sets the nextRunAfter.
	 *
	 * @param nextRunAfter New nextRunAfter.
	 */
	public void setNextRunAfter(
			final LocalDateTime nextRunAfter) {
		this.nextRunAfter = nextRunAfter;
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
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (this.getClass() != obj.getClass())) {
			return false;
		}
		final LocalPeriodicJobCacheEntry other = (LocalPeriodicJobCacheEntry) obj;
		return Objects.equals(this.lastRunAt, other.lastRunAt) && Objects.equals(this.nextRunAfter, other.nextRunAfter);
	}

}
