package org.coldis.library.test.helper;

import org.coldis.library.helper.EnumHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Enum helper test.
 */
public class EnumHelperTest {

	/**
	 * Test data.
	 */
	private static final Object[][] TEST_DATA = { { TestEnum.ENUM1, 0 }, { TestEnum.ENUM1, 1 }, { TestEnum.ENUM1, 2 },
					{ TestEnum.ENUM1, 3 }, { TestEnum.ENUM1, 4 }, { TestEnum.ENUM1, 5 }, { TestEnum.ENUM2, 6 },
					{ TestEnum.ENUM2, 7 }, { TestEnum.ENUM2, 8 }, { TestEnum.ENUM2, 9 }, { TestEnum.ENUM2, 10 },
					{ TestEnum.ENUM2, 11 }, { TestEnum.ENUM3, 12 }, { TestEnum.ENUM3, 13 }, { TestEnum.ENUM3, 14 },
					{ TestEnum.ENUM3, 15 }, { TestEnum.ENUM3, 16 }, { TestEnum.ENUM3, 17 }, { TestEnum.ENUM4, 18 },
					{ TestEnum.ENUM4, 19 }, { TestEnum.ENUM4, 20 }, { TestEnum.ENUM4, 21 }, { TestEnum.ENUM4, 22 },
					{ TestEnum.ENUM4, 23 }, { TestEnum.ENUM5, 24 }, { TestEnum.ENUM5, 25 }, { TestEnum.ENUM5, 26 },
					{ TestEnum.ENUM5, 27 }, { TestEnum.ENUM5, 28 }, { TestEnum.ENUM5, 29 }, { null, null } };

	/**
	 * Tests the get by id.
	 */
	@Test
	public void getById() {
		// Gets the enum for each possible id.
		final TestEnum enum1 = EnumHelper.getById(TestEnum.class, 1L);
		final TestEnum enum2 = EnumHelper.getById(TestEnum.class, 2L);
		final TestEnum enum3 = EnumHelper.getById(TestEnum.class, 3L);
		final TestEnum enum4 = EnumHelper.getById(TestEnum.class, 4L);
		final TestEnum enum5 = EnumHelper.getById(TestEnum.class, 5L);
		final TestEnum enum6 = EnumHelper.getById(TestEnum.class, null);
		// Asserts that the enums are the expected.
		Assertions.assertEquals(TestEnum.ENUM1, enum1);
		Assertions.assertEquals(TestEnum.ENUM2, enum2);
		Assertions.assertEquals(TestEnum.ENUM3, enum3);
		Assertions.assertEquals(TestEnum.ENUM4, enum4);
		Assertions.assertEquals(TestEnum.ENUM5, enum5);
		Assertions.assertEquals(null, enum6);
	}

	/**
	 * Tests the get by attribute (equals to property).
	 */
	@Test
	public void getByAttribute() {
		// Gets the enum for each possible id.
		final TestEnum enum1 = EnumHelper.getByAttribute(TestEnum.class, "id", 1L);
		final TestEnum enum2 = EnumHelper.getByAttribute(TestEnum.class, "id", 2L);
		final TestEnum enum3 = EnumHelper.getByAttribute(TestEnum.class, "id", 3L);
		final TestEnum enum4 = EnumHelper.getByAttribute(TestEnum.class, "id", 4L);
		final TestEnum enum5 = EnumHelper.getByAttribute(TestEnum.class, "id", 5L);
		final TestEnum enum6 = EnumHelper.getByAttribute(TestEnum.class, "id", null);
		// Asserts that the enums are the expected.
		Assertions.assertEquals(TestEnum.ENUM1, enum1);
		Assertions.assertEquals(TestEnum.ENUM2, enum2);
		Assertions.assertEquals(TestEnum.ENUM3, enum3);
		Assertions.assertEquals(TestEnum.ENUM4, enum4);
		Assertions.assertEquals(TestEnum.ENUM5, enum5);
		Assertions.assertEquals(null, enum6);
	}

	/**
	 * Tests the get by range (between two properties).
	 */
	@Test
	public void getByRange() {
		// For each range test object.
		for (final Object[] testObj : EnumHelperTest.TEST_DATA) {
			// Creates a new enum by range.
			final TestEnum testEnum = EnumHelper.getByRange(TestEnum.class, "floor", "ceil", testObj[1]);
			// Asserts that the enum has the expected value.
			Assertions.assertEquals(testObj[0], testEnum);
		}
	}

}
