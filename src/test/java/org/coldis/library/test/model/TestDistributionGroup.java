package org.coldis.library.test.model;

import java.util.Objects;

import org.coldis.library.model.AbstractDistributionGroup;
import org.coldis.library.model.Identifiable;

/**
 * Distribution.
 */
public class TestDistributionGroup extends AbstractDistributionGroup implements Identifiable {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 352525798848042383L;

	/**
	 * Id.
	 */
	private Long id;

	/**
	 * Gets the id.
	 * 
	 * @return The id.
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id New id.
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(id);
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof TestDistributionGroup)) {
			return false;
		}
		TestDistributionGroup other = (TestDistributionGroup) obj;
		return Objects.equals(id, other.id);
	}

}
