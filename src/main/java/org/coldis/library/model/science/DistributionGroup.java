package org.coldis.library.model.science;

import org.coldis.library.model.Expirable;
import org.coldis.library.model.Primaryable;

/**
 * Distribution group.
 */
public interface DistributionGroup extends Primaryable, Expirable, Comparable<DistributionGroup> {

	/**
	 * Gets the distribution size.
	 *
	 * @return The distribution size.
	 */
	Integer getDistributionSize();

	/**
	 * Sets the distribution size.
	 *
	 * @param size The distribution size.
	 */
	void setDistributionSize(
			Integer size);

	/**
	 * Gets the current size.
	 *
	 * @return The current size.
	 */
	Long getCurrentSize();

	/**
	 * Sets the current size.
	 *
	 * @param currentSize The current size.
	 */
	void setCurrentSize(
			Long currentSize);

	/**
	 * Gets the (absolute) limit.
	 *
	 * @return The (absolute) limit.
	 */
	Long getAbsoluteLimit();

	/**
	 * Sets the (absolute) limit.
	 *
	 * @param absoluteLimit The (absolute) limit.
	 */
	void setAbsoluteLimit(
			Long absoluteLimit);

}
