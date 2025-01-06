package org.coldis.library.model.science;

import java.util.List;

import org.coldis.library.model.Primaryable;

/**
 * Simple distribution.
 */
public class SimpleDistribution extends AbstractDistribution {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 9101491398722597248L;

	/**
	 * No arguments constructor.
	 */
	public SimpleDistribution() {
		super();
	}

	/**
	 * Default constructor.
	 *
	 * @param groups Groups.
	 */
	public SimpleDistribution(final List<DistributionGroup> groups) {
		super();
		this.setGroups(groups);
	}

}
