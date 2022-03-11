package org.coldis.library.model.science;

import java.util.ArrayList;
import java.util.List;

/**
 * Distribution group.
 */
public abstract class AbstractDistribution implements Distribution {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 8571810136988594875L;

	/**
	 * Base (relative) size for the distribution. Used to split group samples.
	 */
	private Integer baseSize;

	/**
	 * Groups.
	 */
	private List<DistributionGroup> groups;

	/**
	 * Gets the baseSize.
	 *
	 * @return The baseSize.
	 */
	@Override
	public Integer getBaseSize() {
		this.baseSize = (this.baseSize == null ? Distribution.getBaseSize(this.getGroups()) : this.baseSize);
		this.baseSize = (this.baseSize != null && this.baseSize > 0 ? this.baseSize : 100);
		return this.baseSize;
	}

	/**
	 * Sets the baseSize.
	 *
	 * @param baseSize New baseSize.
	 */
	@Override
	public void setBaseSize(
			final Integer baseSize) {
		this.baseSize = baseSize;
	}

	/**
	 * Gets the groups.
	 *
	 * @return The groups.
	 */
	@Override
	public List<DistributionGroup> getGroups() {
		// Makes sure the list is initialized.
		this.groups = (this.groups == null ? new ArrayList<>() : this.groups);
		// Returns the list.
		return this.groups;
	}

	/**
	 * Sets the groups.
	 *
	 * @param groups New groups.
	 */
	@Override
	public void setGroups(
			final List<DistributionGroup> groups) {
		this.groups = groups;
	}

}
