package org.coldis.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Object that has an expiration date/time.
 */
public interface Expirable extends Serializable {

	/**
	 * Gets the object expiration date/time.
	 *
	 * @return The object expiration date/time.
	 */
	LocalDateTime getExpiredAt();

	/**
	 * Sets the object expiration date/time.
	 *
	 * @param expiredAt The object expiration date/time.
	 */
	void setExpiredAt(final LocalDateTime expiredAt);

	/**
	 * Returns if the object has expired.
	 *
	 * @return If the object has expired.
	 */
	Boolean getExpired();

}
