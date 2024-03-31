package org.coldis.library.test.helper;

import org.coldis.library.helper.RandomHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Random helper test.
 */
public class RandomHelperTest {

	/**
	 * Tests getting a random string.
	 */
	@Test
	public void testGetRandomString() {
		for (Integer i = 0; i < 100; i++) {
			// Random string.
			final String randomString = RandomHelper.getRandomString(100, RandomHelper.ALPHA_NUMERIC_CHARS);
			// Asserts the random string.
			Assertions.assertNotNull(randomString);
			Assertions.assertEquals(100, randomString.length());
			Assertions.assertTrue(randomString.matches("[" + RandomHelper.ALPHA_NUMERIC_CHARS + "]*"));
		}
	}

	/**
	 * Tests getting a random number as string.
	 */
	@Test
	public void testGetRandomNumber() {
		for (Integer i = 0; i < 100; i++) {
			// Random number.
			final Long randomNumber = RandomHelper.getPositiveRandomLong(100L);
			// Asserts the random number.
			Assertions.assertNotNull(randomNumber);
			Assertions.assertEquals(100 >= randomNumber, true);
		}
	}

}
