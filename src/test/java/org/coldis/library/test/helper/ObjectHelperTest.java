package org.coldis.library.test.helper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.coldis.library.helper.ObjectHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Object helper test.
 */
public class ObjectHelperTest {

	/**
	 * Tests shallow copying an object.
	 */
	@Test
	public void testShallowCopyAttributes() {
		// Test data.
		final TestClass source = new TestClass("source", 1L, new TestClass("source1", 11L, null, null, null, Map.of("1", 1)),
				new TestClass("source1", 12L, null, null, null, Map.of("2", 2)), BigDecimal.ONE, Map.of());
		final TestClass target = new TestClass("target", 2L, null, null, null, Map.of());
		// Shallow copy the attributes.
		ObjectHelper.copyAttributes(source, target, false, false, null, null);
		// Makes sure the source and target are equals now.
		Assertions.assertEquals(source, target);
		Assertions.assertFalse(source == target);
	}

	/**
	 * Tests deep copying an object, but ignoring attributes.
	 */
	@Test
	public void testDeepCopyAttributesIgnoring() {
		// Test data.
		final TestClass source = new TestClass("source", 1L, new TestClass("source1", 11L, null, null, null, Map.of("test1", 1, "test2", 2)),
				new TestClass("source1", 12L, null, null, null, Map.of()), null, Map.of());
		final TestClass target = new TestClass("target", 2L, new TestClass(null, null, null, null, null, new HashMap<>()), null, null, Map.of());
		// Shallow copy the attributes.
		ObjectHelper.copyAttributes(source, target, true, false, Set.of("test3.test1", "test3.test6.test1", "test4"), null);
		// Makes sure the source and target are not equals.
		Assertions.assertNotEquals(source, target);
		// Asserts that the right attributes were ignored.
		Assertions.assertEquals(source.getTest1(), target.getTest1());
		Assertions.assertEquals(source.getTest2(), target.getTest2());
		Assertions.assertEquals(source.getTest3().getTest2(), target.getTest3().getTest2());
		Assertions.assertEquals(source.getTest3().getTest3(), target.getTest3().getTest3());
		Assertions.assertEquals(source.getTest3().getTest4(), target.getTest3().getTest4());
		Assertions.assertNull(target.getTest3().getTest1());
		Assertions.assertNull(target.getTest3().getTest6().get("test1"));
		Assertions.assertEquals(source.getTest3().getTest6().get("test2"), target.getTest3().getTest6().get("test2"));
		Assertions.assertNull(target.getTest4());

	}

	/**
	 * Tests deep copying an object using source conditions.
	 */
	@Test
	public void testDeepCopyAttributesUsingConditions() {
		// Test data.
		final TestClass source = new TestClass("source", 1L,
				new TestClass("source1", 11L, null, null, null, Map.of("test1", 1, "test2", 2, "test3", "", "test4", Map.of("1", 1))),
				new TestClass("source1", 12L, null, null, null, Map.of("2", 2)), BigDecimal.ONE, Map.of());
		final TestClass target = new TestClass("target", 2L, new TestClass(), new TestClass("target1", 22L, new TestClass(), null, null, Map.of()),
				BigDecimal.ZERO, Map.of());
		// Shallow copy the attributes.
		ObjectHelper.copyAttributes(source, target, true, true, Set.of("test3.test6.test2"), ((
				getter,
				sourceValue,
				targetValue) -> ((!ObjectHelper.isEmpty(sourceValue)) && (targetValue == null))));
		// Makes sure the source and target are not equals.
		Assertions.assertNotEquals(source, target);
		// Asserts that the source conditions were followed when copying attributes.
		Assertions.assertNotEquals(source.getTest1(), target.getTest1());
		Assertions.assertEquals(2L, target.getTest2());
		Assertions.assertEquals(new TestClass("source1", 11L, null, null, null, Map.of("test1", 1, "test3", "", "test4", Map.of("1", 1))), target.getTest3());
		Assertions.assertEquals("target1", target.getTest4().getTest1());
		Assertions.assertNotEquals(source.getTest4().getTest2(), target.getTest4().getTest2());
		Assertions.assertEquals(new TestClass(), target.getTest4().getTest3());
		Assertions.assertEquals(source.getTest4().getTest4(), target.getTest4().getTest4());
		Assertions.assertNotEquals(source.getTest5(), target.getTest5());
	}

}
