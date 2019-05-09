package org.coldis.library.model;

import java.time.LocalDateTime;

/**
 * Abstract time-stamped and expirable object.
 */
public abstract class AbstractTimestampedExpirableObject extends AbstractExpirableObject implements TimestampedObject {

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
	 * @see org.coldis.library.model.TimestampedObject#getCreatedAt()
	 */
	@Override
	public LocalDateTime getCreatedAt() {
		return this.createdAt;
	}

	/**
	 * @see org.coldis.library.model.TimestampedObject#setCreatedAt(java.time.LocalDateTime)
	 */
	@Override
	public void setCreatedAt(final LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @see org.coldis.library.model.TimestampedObject#getUpdatedAt()
	 */
	@Override
	public LocalDateTime getUpdatedAt() {
		return this.updatedAt;
	}

	/**
	 * @see org.coldis.library.model.TimestampedObject#setUpdatedAt(java.time.LocalDateTime)
	 */
	@Override
	public void setUpdatedAt(final LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
