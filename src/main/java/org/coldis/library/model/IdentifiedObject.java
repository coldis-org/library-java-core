package org.coldis.library.model;

import java.io.Serializable;

/**
 * Identified object.
 */
public interface IdentifiedObject extends Serializable {

	/**
	 * Gets the identifier.
	 *
	 * @return The identifier.
	 */
	public Long getId();

}
