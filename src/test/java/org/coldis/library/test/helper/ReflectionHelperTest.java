package org.coldis.library.test.helper;

import java.util.List;

import org.coldis.library.helper.ReflectionHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Reflection helper test.
 */
public class ReflectionHelperTest {

	/**
	 * Test data.
	 */
	private static final List<TestClass> TEST_DATA = List.of(
			new TestClass("1", 1l, new TestClass("1", 1L, new TestClass("1", 1L, null, null, null, null), null, null, null),
					new TestClass("1", 1L, null, null, null, null), null, null),
			new TestClass("2", 2l, new TestClass("2", 2L, new TestClass("2", 2L, null, null, null, null), null, null, null),
					new TestClass("2", 2L, null, null, null, null), null, null));

	/**
	 * Tests setting attributes in complex object trees.
	 */
	@Test
	public void testSetAttribute() {
		// For each test object.
		for (final TestClass test : ReflectionHelperTest.TEST_DATA) {
			// Sets an attribute value.
			ReflectionHelper.setAttribute(test, "test1", "100");
			// Makes sure the attribute has been updated.
			Assertions.assertEquals("100", test.getTest1());
			Assertions.assertEquals("100", ReflectionHelper.getAttribute(test, "test1"));
			// Sets an attribute value.
			ReflectionHelper.setAttribute(test, "test2", 100L);
			// Makes sure the attribute has been updated.
			Assertions.assertEquals(100L, test.getTest2().longValue());
			Assertions.assertEquals(100L, ReflectionHelper.getAttribute(test, "test2"));
			// Sets an attribute value.
			ReflectionHelper.setAttribute(test, "test4.test1", "100");
			// Makes sure the attribute has been updated.
			Assertions.assertEquals("100", test.getTest4().getTest1());
			Assertions.assertEquals("100", ReflectionHelper.getAttribute(test, "test4.test1"));
			// Sets an attribute value.
			ReflectionHelper.setAttribute(test, "test4.test2", 100L);
			// Makes sure the attribute has been updated.
			Assertions.assertEquals(100L, test.getTest4().getTest2().longValue());
			Assertions.assertEquals(100L, ReflectionHelper.getAttribute(test, "test4.test2"));
			// Sets an attribute value.
			ReflectionHelper.setAttribute(test, "test3.test3.test1", "100");
			// Makes sure the attribute has been updated.
			Assertions.assertEquals("100", test.getTest3().getTest3().getTest1());
			Assertions.assertEquals("100", ReflectionHelper.getAttribute(test, "test3.test3.test1"));
			// Sets an attribute value.
			ReflectionHelper.setAttribute(test, "test3.test3.test2", 100L);
			// Makes sure the attribute has been updated.
			Assertions.assertEquals(100L, test.getTest3().getTest3().getTest2().longValue());
			Assertions.assertEquals(100L, ReflectionHelper.getAttribute(test, "test3.test3.test2"));
		}
	}

}
