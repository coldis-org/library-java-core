package org.coldis.library.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Typed object (type name is an attribute).
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "typeName",
visible = true)
public interface Typable extends Serializable {

	/**
	 * Gets the type name of the object.
	 *
	 * @return The type name of the object.
	 */
	@JsonProperty
	String getTypeName();

}
