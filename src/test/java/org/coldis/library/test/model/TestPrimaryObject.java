package org.coldis.library.test.model;

import java.util.Objects;

import org.coldis.library.model.IdentifiedObject;
import org.coldis.library.model.OptionallyPrimaryItem;

/**
 * Test object.
 */
public class TestPrimaryObject implements IdentifiedObject, OptionallyPrimaryItem {

	/**
	 * Generated serial.
	 */
	private static final long serialVersionUID = -2416083665100176376L;

	/**
	 * Identifier.
	 */
	private Long id;

	/**
	 * If this is a primary object.
	 */
	private Boolean primary = false;

	/**
	 * Default constructor.
	 * 
	 * @param id Id.
	 */
	public TestPrimaryObject(Long id) {
		super();
		this.id = id;
	}

	/**
	 * Gets the id.
	 * 
	 * @return The id.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id New id.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the primary.
	 * 
	 * @return The primary.
	 */
	public Boolean getPrimary() {
		return primary;
	}

	/**
	 * Sets the primary.
	 * 
	 * @param primary New primary.
	 */
	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TestPrimaryObject)) {
			return false;
		}
		TestPrimaryObject other = (TestPrimaryObject) obj;
		return Objects.equals(id, other.id);
	}

}
