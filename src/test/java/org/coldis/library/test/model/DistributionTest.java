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
		SimpleDistribution distribution = new SimpleDistribution();
		distribution.setBaseSize(100);
		TestDistributionGroup firstGroup = new TestDistributionGroup();
		firstGroup.setId(1L);
		firstGroup.setDistributionSize(2);
		firstGroup.setAbsoluteLimit(2L);
		TestDistributionGroup secondGroup = new TestDistributionGroup();
		secondGroup.setId(2L);
		secondGroup.setDistributionSize(3);
		secondGroup.setAbsoluteLimit(3L);
		TestDistributionGroup expiredGroup = new TestDistributionGroup();
		expiredGroup.setId(3L);
		expiredGroup.setDistributionSize(10);
		expiredGroup.setAbsoluteLimit(100L);
		expiredGroup.setExpiredAt(DateTimeHelper.getCurrentLocalDateTime());
		distribution.setGroups(List.of(firstGroup, secondGroup, expiredGroup));
		// Distributes a few samples to groups one and two.
		Assertions.assertEquals(secondGroup, distribution.distribute(4L));
		Assertions.assertEquals(secondGroup, distribution.distribute(2L));
		Assertions.assertEquals(firstGroup, distribution.distribute(0L));
		Assertions.assertEquals(firstGroup, distribution.distribute(0L));
		Assertions.assertEquals(2, firstGroup.getCurrentSize());
		Assertions.assertEquals(2, secondGroup.getCurrentSize());
		Assertions.assertEquals(secondGroup, distribution.distribute(0L));
		Assertions.assertEquals(3, secondGroup.getCurrentSize());

	}

}
