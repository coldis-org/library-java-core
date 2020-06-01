package org.coldis.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Time-stamped object.
 */
public interface Timestampable extends Serializable {

	/**
	 * Gets the object creation date/time.
	 *
	 * @return The object creation date/time.
	 */
	LocalDateTime getCreatedAt();

	/**
	 * Sets the object creation date/time.
	 *
	 * @param createdAt New object creation date/time.
	 */
	void setCreatedAt(final LocalDateTime createdAt);

	/**
	 * Gets the object last update date/time.
	 *
	 * @return The object last update date/time.
	 */
	LocalDateTime getUpdatedAt();

	/**
	 * Sets the object last update date/time.
	 *
	 * @param updatedAt New object last update date/time.
	 */
	void setUpdatedAt(final LocalDateTime updatedAt);

}