package org.coldis.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract time-stamped and expirable object.
 */
public abstract class AbstractTimestampableExpirable extends AbstractExpirable implements Timestampable {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 639375333979696110L;

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
		return this.createdAt;
	}

	/**
	 * @see org.coldis.library.model.Timestampable#setCreatedAt(java.time.LocalDateTime)
	 */
	@Override
	public void setCreatedAt(final LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @see org.coldis.library.model.Timestampable#getUpdatedAt()
	 */
	@Override
	public LocalDateTime getUpdatedAt() {
		return this.updatedAt;
	}

	/**
	 * @see org.coldis.library.model.Timestampable#setUpdatedAt(java.time.LocalDateTime)
	 */
	@Override
	public void setUpdatedAt(final LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + Objects.hash(this.createdAt, this.updatedAt);
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof AbstractTimestampableExpirable)) {
			return false;
		}
		final AbstractTimestampableExpirable other = (AbstractTimestampableExpirable) obj;
		return Objects.equals(this.createdAt, other.createdAt) && Objects.equals(this.updatedAt, other.updatedAt);
	}

}
