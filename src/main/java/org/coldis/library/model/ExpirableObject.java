package org.coldis.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Object that has expiration date.
 */
public interface ExpirableObject extends Serializable {

	/**
	 * Gets the expiredAt.
	 *
	 * @return The expiredAt.
	 */
	LocalDateTime getExpiredAt();

	/**
	 * Sets the expiredAt.
	 *
	 * @param expiredAt New expiredAt.
	 */
	void setExpiredAt(final LocalDateTime expiredAt);

	/**
	 * Returns if the object is expired.
	 *
	 * @return If the object is expired.
	 */
	public Boolean getExpired();

}
