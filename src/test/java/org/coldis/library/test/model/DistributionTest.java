package org.coldis.library.test.model;

import java.util.List;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.model.science.SimpleDistribution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Distribution test.
 */
public class DistributionTest {

	/**
	 * Tests distributions.
	 */
	@Test
	public void testDistribution() {
		// Creates a new distribution.
		final SimpleDistribution distribution = new SimpleDistribution();
		distribution.setBaseSize(100);
		final TestDistributionGroup firstGroup = new TestDistributionGroup();
		firstGroup.setId(1L);
		firstGroup.setDistributionSize(2);
		firstGroup.setAbsoluteLimit(2L);
		final TestDistributionGroup secondGroup = new TestDistributionGroup();
		secondGroup.setId(2L);
		secondGroup.setDistributionSize(3);
		secondGroup.setAbsoluteLimit(3L);
		final TestDistributionGroup expiredGroup = new TestDistributionGroup();
		expiredGroup.setId(3L);
		expiredGroup.setDistributionSize(10);
		expiredGroup.setAbsoluteLimit(100L);
		expiredGroup.setExpiredAt(DateTimeHelper.getCurrentLocalDateTime());
		distribution.setGroups(List.of(firstGroup, secondGroup, expiredGroup));
		final TestDistributionGroup zeroDistributionGroup = new TestDistributionGroup();
		expiredGroup.setId(3L);
		expiredGroup.setDistributionSize(0);
		expiredGroup.setAbsoluteLimit(100L);
		distribution.setGroups(List.of(firstGroup, secondGroup, expiredGroup, zeroDistributionGroup));
		// Distributes a few samples to groups one and two.
		Assertions.assertEquals(secondGroup, distribution.distribute(1004L, true));
		Assertions.assertEquals(secondGroup, distribution.distribute(2L, true));
		Assertions.assertEquals(firstGroup, distribution.distribute(0L, true));
		Assertions.assertEquals(firstGroup, distribution.distribute(0L, true));
		Assertions.assertEquals(2, firstGroup.getCurrentSize());
		Assertions.assertEquals(2, secondGroup.getCurrentSize());
		Assertions.assertEquals(secondGroup, distribution.distribute(0L, true));
		Assertions.assertEquals(3, secondGroup.getCurrentSize());

	}

}
