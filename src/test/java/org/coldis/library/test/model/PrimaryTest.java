package org.coldis.library.test.model;

import java.util.ArrayList;
import java.util.Collection;

import org.coldis.library.model.Primaryable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Primary item test.
 */
public class PrimaryTest {

	/**
	 * Test data.
	 */
	private static final TestPrimaryObject[] TEST_DATA = { new TestPrimaryObject(1L), new TestPrimaryObject(2L),
			new TestPrimaryObject(3L), new TestPrimaryObject(4L), new TestPrimaryObject(5L) };

	/**
	 * Tests a primary item collection.
	 */
	@Test
	public void test00PrimaryItemCollection() {
		// Creates a new collection.
		Collection<TestPrimaryObject> itemCollection = new ArrayList<>();
		// Makes sure methods do not fail on a empty collection.
		Primaryable.autoAssignPrimary(itemCollection);
		Primaryable.add(itemCollection, null, false);
		Primaryable.remove(itemCollection, null);
		// Makes sure there is no primary item in the collection.
		Assertions.assertNull(Primaryable.getPrimary(itemCollection));
		// Makes sure the first test item is not primary.
		Assertions.assertFalse(TEST_DATA[0].getPrimary());
		// Adds the two non-primary items to the collection.
		itemCollection.add(new TestPrimaryObject(TEST_DATA[0].getId()));
		itemCollection.add(new TestPrimaryObject(TEST_DATA[1].getId()));
		// Makes sure the auto assign primary works.
		Assertions.assertNull(Primaryable.getPrimary(itemCollection));
		Primaryable.autoAssignPrimary(itemCollection);
		Assertions.assertEquals(TEST_DATA[0], Primaryable.getPrimary(itemCollection));
		Assertions.assertEquals(2, itemCollection.size());
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[0]));
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[1]));
		Assertions.assertEquals(1, itemCollection.stream().filter(Primaryable::getPrimary).count());
		// Empty the list.
		itemCollection = new ArrayList<>();
		// Adds the two non-primary items to the collection (now using the static
		// method).
		Primaryable.add(itemCollection, new TestPrimaryObject(TEST_DATA[1].getId()), false);
		Primaryable.add(itemCollection, new TestPrimaryObject(TEST_DATA[0].getId()), false);
		// Makes sure the add with auto assign primary works.
		Assertions.assertEquals(TEST_DATA[1], Primaryable.getPrimary(itemCollection));
		Assertions.assertEquals(2, itemCollection.size());
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[0]));
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[1]));
		Assertions.assertEquals(1, itemCollection.stream().filter(Primaryable::getPrimary).count());
		// Removes the non primary item.
		Primaryable.remove(itemCollection, TEST_DATA[0]);
		// Makes sure remove works and left item is still primary.
		Assertions.assertEquals(TEST_DATA[1], Primaryable.getPrimary(itemCollection));
		Assertions.assertEquals(1, itemCollection.size());
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[1]));
		Assertions.assertEquals(1, itemCollection.stream().filter(Primaryable::getPrimary).count());
		// Re-adds the non-primary item.
		Primaryable.add(itemCollection, new TestPrimaryObject(TEST_DATA[0].getId()), true);
		// Makes sure nothing has changed.
		Assertions.assertEquals(TEST_DATA[1], Primaryable.getPrimary(itemCollection));
		Assertions.assertEquals(2, itemCollection.size());
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[0]));
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[1]));
		Assertions.assertEquals(1, itemCollection.stream().filter(Primaryable::getPrimary).count());
		// Re-adds the non-primary item (now, as primary).
		TestPrimaryObject testPrimaryObject1 = new TestPrimaryObject(TEST_DATA[0].getId());
		testPrimaryObject1.setPrimary(true);
		Primaryable.add(itemCollection, testPrimaryObject1, true);
		// Makes sure the primary item has changed.
		Assertions.assertEquals(TEST_DATA[0], Primaryable.getPrimary(itemCollection));
		Assertions.assertEquals(2, itemCollection.size());
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[0]));
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[1]));
		Assertions.assertEquals(1, itemCollection.stream().filter(Primaryable::getPrimary).count());
		// Removes the primary item.
		Primaryable.remove(itemCollection, TEST_DATA[0]);
		// Makes sure the primary object is now the last one.
		Assertions.assertEquals(TEST_DATA[1], Primaryable.getPrimary(itemCollection));
		Assertions.assertEquals(1, itemCollection.size());
		Assertions.assertTrue(itemCollection.contains(TEST_DATA[1]));
		Assertions.assertEquals(1, itemCollection.stream().filter(Primaryable::getPrimary).count());

	}

}