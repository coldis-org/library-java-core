package org.coldis.library.model.science;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.coldis.library.model.Primaryable;

/**
 * Distribution.
 */
public interface Distribution extends Serializable {

	/**
	 * Random.
	 */
	Random RANDOM = new Random();

	/**
	 * Gets the base (relative) size for the distribution. Used to split group
	 * samples.
	 *
	 * @param  groups Groups.
	 * @return        The base (relative) size for the distribution.
	 */
	static Integer getBaseSize(
			final List<DistributionGroup> groups) {
		return groups == null ? 100 : groups.stream().map(DistributionGroup::getDistributionSize).reduce(0, Integer::sum);
	}

	/**
	 * Gets the base (relative) size for the distribution. Used to split group
	 * samples.
	 *
	 * @return The base (relative) size for the distribution.
	 */
	default Integer getBaseSize() {
		return Distribution.getBaseSize(this.getGroups());
	}

	/**
	 * Sets the base (relative) size for the distribution. Used to split group
	 * samples.
	 *
	 * @param baseSize The base (relative) size for the distribution.
	 */
	default void setBaseSize(
			final Integer baseSize) {
	}

	/**
	 * Gets the groups of the distribution.
	 *
	 * @return The groups of the distribution.
	 */
	List<DistributionGroup> getGroups();

	/**
	 * Sets the groups of the distribution.
	 *
	 * @param groups The groups of the distribution.
	 */
	void setGroups(
			List<DistributionGroup> groups);

	/**
	 * Distributes a sample against groups.
	 *
	 * @param  pseudoRandomSampleId The (pseudo) random identification of the sample
	 *                                  item.
	 * @return                      The selected group for the sample.
	 */
	default DistributionGroup distribute(
			final Long pseudoRandomSampleId) {
		return Distribution.distribute(this.getGroups(), this.getBaseSize(), pseudoRandomSampleId);
	}

	/**
	 * Distributes a sample against groups.
	 *
	 * @param  groups               The available groups.
	 * @param  baseSize             The base (relative) size of distribution.
	 * @param  pseudoRandomSampleId The (pseudo) random identification of the sample
	 *                                  item.
	 * @return                      The selected group for the sample.
	 */
	static DistributionGroup distribute(
			final List<DistributionGroup> groups,
			final Integer baseSize,
			final Long pseudoRandomSampleId) {
		// Sorts the list.
		Collections.sort(groups);
		// Makes sure there is a primary group.
		Primaryable.autoAssignPrimary(groups);
		// Gets a list without the primary.
		final DistributionGroup primaryGroup = Primaryable.getPrimary(groups);
		final List<DistributionGroup> nonPrimaryNonExpiredGroups = groups.stream().filter(group -> !group.getPrimary() && !group.getExpired())
				.collect(Collectors.toList());
		// Group for the sample to be distributed.
		DistributionGroup selectedGroup = null;
		// Group size sum and group.
		int groupSizeSum = 0;
		final long groupSelection = (Math.abs(pseudoRandomSampleId) % baseSize);
		// For each non-primary group.
		for (final DistributionGroup currentGroup : nonPrimaryNonExpiredGroups) {
			// If the current group threshold is greater that the group selection.
			final Integer distributionSize = (currentGroup.getDistributionSize() == null ? 0 : currentGroup.getDistributionSize());
			if ((groupSizeSum + distributionSize) > groupSelection) {
				// The current group is selected.
				selectedGroup = currentGroup;
				break;
			}
			// Increments the group size sum.
			groupSizeSum = (groupSizeSum + distributionSize);
		}
		// If none of the non-primary groups was selected.
		if (selectedGroup == null) {
			// The selected group is the primary group.
			selectedGroup = primaryGroup;
		}
		// Increments the selected group size.
		final Long currentSize = (selectedGroup.getCurrentSize() == null ? 0 : selectedGroup.getCurrentSize());
		selectedGroup.setCurrentSize(currentSize + 1);
		// Returns the selected group.
		return selectedGroup;
	}
}
