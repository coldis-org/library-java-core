package org.coldis.library.test.helper;

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
		final TestClass source = new TestClass("source", 1L, new TestClass("source1", 11L, null, null), new TestClass("source1", 12L, null, null));
		final TestClass target = new TestClass("target", 2L, null, null);
		// Shallow copy the attributes.
		ObjectHelper.copyAttributes(source, target, false, false, null, null, null);
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
		final TestClass source = new TestClass("source", 1L, new TestClass("source1", 11L, null, null), new TestClass("source1", 12L, null, null));
		final TestClass target = new TestClass("target", 2L, new TestClass(), new TestClass());
		// Shallow copy the attributes.
		ObjectHelper.copyAttributes(source, target, true, false, Set.of("test3.test1", "test4"), null, null);
		// Makes sure the source and target are not equals.
		Assertions.assertNotEquals(source, target);
		// Asserts that the right attributes were ignored.
		Assertions.assertEquals(source.getTest1(), target.getTest1());
		Assertions.assertEquals(source.getTest2(), target.getTest2());
		Assertions.assertEquals(source.getTest3().getTest2(), target.getTest3().getTest2());
		Assertions.assertEquals(source.getTest3().getTest3(), target.getTest3().getTest3());
		Assertions.assertEquals(source.getTest3().getTest4(), target.getTest3().getTest4());
		Assertions.assertNull(target.getTest3().getTest1());
		Assertions.assertEquals(new TestClass(), target.getTest4());

	}

	/**
	 * Tests deep copying an object using source conditions.
	 */
	@Test
	public void testDeepCopyAttributesUsingSourceConditions() {
		// Test data.
		final TestClass source = new TestClass("source", null, null, new TestClass(null, 12L, null, new TestClass("source1", 13L, null, null)));
		final TestClass target = new TestClass("target", 2L, new TestClass(), new TestClass("target1", 22L, new TestClass(), null));
		// Shallow copy the attributes.
		ObjectHelper.copyAttributes(source, target, true, true, null, (getter, value) -> (value != null), null);
		// Makes sure the source and target are not equals.
		Assertions.assertNotEquals(source, target);
		// Asserts that the source conditions were followed when copying attributes.
		Assertions.assertEquals(source.getTest1(), target.getTest1());
		Assertions.assertEquals(2L, target.getTest2());
		Assertions.assertEquals(new TestClass(), target.getTest3());
		Assertions.assertEquals("target1", target.getTest4().getTest1());
		Assertions.assertEquals(source.getTest4().getTest2(), target.getTest4().getTest2());
		Assertions.assertEquals(new TestClass(), target.getTest4().getTest3());
		Assertions.assertEquals(source.getTest4().getTest4(), target.getTest4().getTest4());
	}

	/**
	 * Tests deep copying an object using source conditions.
	 */
	@Test
	public void testDeepCopyAttributesUsingTargetConditions() {
		// Test data.
		final TestClass source = new TestClass("source", 1L, new TestClass(),
				new TestClass("source1", 12L, new TestClass(), new TestClass("source1", 13L, null, null)));
		final TestClass target = new TestClass(null, 2L, null, new TestClass(null, 22L, null, null));
		// Shallow copy the attributes.
		ObjectHelper.copyAttributes(source, target, true, true, null, null, (getter, value) -> (value == null));
		// Makes sure the source and target are not equals.
		Assertions.assertNotEquals(source, target);
		// Asserts that the source conditions were followed when copying attributes.
		Assertions.assertEquals(source.getTest1(), target.getTest1());
		Assertions.assertEquals(2L, target.getTest2());
		Assertions.assertEquals(source.getTest3(), target.getTest3());
		Assertions.assertEquals(source.getTest4().getTest1(), target.getTest4().getTest1());
		Assertions.assertEquals(22L, target.getTest4().getTest2());
		Assertions.assertEquals(source.getTest4().getTest3(), target.getTest4().getTest3());
		Assertions.assertEquals(source.getTest4().getTest4(), target.getTest4().getTest4());
	}

}
