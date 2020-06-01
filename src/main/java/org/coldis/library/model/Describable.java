package org.coldis.library.model;

import java.io.Serializable;

/**
 * Describable object.
 */
public interface Describable extends Serializable {

	/**
	 * Gets the description of the object.
	 *
	 * @return The description of the object.
	 */
	String getDescription();

	/**
	 * Sets the description of the object.
	 *
	 * @param description Description of the object.
	 */
	void setDescription(String description);

}
