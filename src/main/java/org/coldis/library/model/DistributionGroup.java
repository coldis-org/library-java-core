package org.coldis.library.model;

import java.math.BigDecimal;

/**
 * Distribution group.
 */
public interface DistributionGroup extends Primaryable {

	/**
	 * Gets the group size.
	 *
	 * @return The group size.
	 */
	BigDecimal getSize();

	/**
	 * Sets the group size.
	 *
	 * @param size The group size.
	 */
	void setSize(BigDecimal size);

	/**
	 * TODO Javadoc
	 * @return Javadoc
	 */
	String getLimit();


}
