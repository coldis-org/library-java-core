package org.coldis.library.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.coldis.library.helper.DateTimeHelper;

/**
 * Abstract object that might expire.
 */
public abstract class AbstractExpirableObject implements ExpirableObject {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 2950109460487681889L;

	/**
	 * Object expiration date/time.
	 */
	private LocalDateTime expiredAt;

	/**
	 * Gets the new expiration value using a base date and a time value.
	 *
	 * @param  expirationBaseDate Expiration base date.
	 * @param  plusUnit           Unit of time to add to the expiration base date.
	 * @param  plusValue          Value of time to add to the expiration base date.
	 *
	 * @return                    The new expiration value using a base date and a
	 *                            time value.
	 */
	public static LocalDateTime getExpirationDate(final LocalDateTime expirationBaseDate, final ChronoUnit plusUnit,
			final Long plusValue) {
		return expirationBaseDate == null ? null : expirationBaseDate.plus(plusValue, plusUnit);
	}

	/**
	 * Updates the expiration date.
	 *
	 * @return The updated expiration date.
	 */
	protected LocalDateTime updateExpiredAt() {
		return this.expiredAt;
	}

	/**
	 * @see org.coldis.library.model.ExpirableObject#getExpiredAt()
	 */
	@Override
	public LocalDateTime getExpiredAt() {
		// Makes sure the expiration date is updated.
		this.expiredAt = this.updateExpiredAt();
		// Returns the expiration date.
		return this.expiredAt;
	}

	/**
	 * @see org.coldis.library.model.ExpirableObject#setExpiredAt(java.time.LocalDateTime)
	 */
	@Override
	public void setExpiredAt(final LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
	}

	/**
	 * If the entity should be considered expired when no expiration date is
	 * assigned.
	 *
	 * @return If the entity should be considered expired when no expiration date is
	 *         assigned.
	 */
	protected Boolean getExpiredByDefault() {
		return true;
	}

	/**
	 * @see org.coldis.library.model.ExpirableObject#getExpired()
	 */
	@Override
	public Boolean getExpired() {
		return ((this.getExpiredAt() == null) ? this.getExpiredByDefault()
				: (DateTimeHelper.getCurrentLocalDateTime().isAfter(this.getExpiredAt())));
	}

}
