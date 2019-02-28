package org.coldis.library.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Typed object (type name is an attribute).
 */
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "typeName", visible = true)
public interface TypedObject extends Serializable {

	/**
	 * Gets the type name of the object.
	 *
	 * @return The type name of the object.
	 */
	@JsonProperty
	String getTypeName();

}
