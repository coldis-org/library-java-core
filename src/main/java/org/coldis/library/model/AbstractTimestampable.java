package org.coldis.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

import org.coldis.library.helper.DateTimeHelper;

/**
 * Abstract time-stamped object.
 */
public abstract class AbstractTimestampable implements Timestampable {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -7189965218508960943L;

	/**
	 * Object creation date/time.
	 */
	private LocalDateTime createdAt;

	/**
	 * Object last update date/time.
	 */
	private LocalDateTime updatedAt;

	/**
	 * @see org.coldis.library.model.Timestampable#getCreatedAt()
	 */
	@Override
	public LocalDateTime getCreatedAt() {
		this.createdAt = (this.createdAt == null ? DateTimeHelper.getCurrentLocalDateTime() : this.createdAt);
		return this.createdAt;
	}

	/**
	 * @see org.coldis.library.model.Timestampable#setCreatedAt(java.time.LocalDateTime)
	 */
	@Override
	public void setCreatedAt(
			final LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @see org.coldis.library.model.Timestampable#getUpdatedAt()
	 */
	@Override
	public LocalDateTime getUpdatedAt() {
		this.updatedAt = (this.updatedAt == null ? DateTimeHelper.getCurrentLocalDateTime() : this.updatedAt);
		return this.updatedAt;
	}

	/**
	 * @see org.coldis.library.model.Timestampable#setUpdatedAt(java.time.LocalDateTime)
	 */
	@Override
	public void setUpdatedAt(
			final LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.createdAt, this.updatedAt);
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
		if (!(obj instanceof AbstractTimestampable)) {
			return false;
		}
		final AbstractTimestampable other = (AbstractTimestampable) obj;
		return Objects.equals(this.createdAt, other.createdAt) && Objects.equals(this.updatedAt, other.updatedAt);
	}

}
