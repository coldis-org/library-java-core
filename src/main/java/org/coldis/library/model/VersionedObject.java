package org.coldis.library.model;

/**
 * Object that can be versioned.
 */
public interface VersionedObject {

	/**
	 * Gets the version of the object.
	 *
	 * @return The version of the object.
	 */
	Long getVersion();
}