package org.coldis.library.model;

import java.io.Serializable;

/**
 * Named object.
 */
public interface Nameable extends Serializable {

	/**
	 * Gets the name of the object.
	 *
	 * @return The name of the object.
	 */
	String getName();

	/**
	 * Sets the name of the object.
	 *
	 * @param name Name of the object.
	 */
	void setName(String name);

}
