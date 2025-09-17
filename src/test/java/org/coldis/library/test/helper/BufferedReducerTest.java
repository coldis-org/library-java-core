package org.coldis.library.test.helper;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.coldis.library.helper.BufferedReducer;
import org.coldis.library.model.Reduceable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Buffered reducer test.
 */
public class BufferedReducerTest {

	/**
	 * Test class.
	 */
	class BufferedReducerTestClass implements Reduceable<BufferedReducerTestClass> {

		/**
		 * Id.
		 */
		private final Integer id;

		/**
		 * Count.
		 */
		private Integer count = 0;

		public BufferedReducerTestClass(final Integer id, final Integer count) {
			super();
			this.id = id;
			this.count = count;
		}

		/**
		 * @see org.coldis.library.model.Reduceable#reduce(java.lang.Object)
		 */
		@Override
		public void reduce(
				final BufferedReducerTestClass toBeReduced) {
			this.count = (this.count + toBeReduced.count);
		}
	}

	/**
	 * Tests reducing an object to a buffer and then to a final repository.
	 */
	@Test
	public void testBufferedReducer() {
		// Tests objects.
		final Map<Integer, BufferedReducerTestClass> reduced = new TreeMap<>();
		final Consumer<BufferedReducerTestClass> reducer = (item -> BufferedReducer.reduce(reduced, item.id, item));
		final BufferedReducer<Integer, BufferedReducerTestClass> buffer = new BufferedReducer<>();

		// Reduces a few occurrences.
		buffer.reduce(1, new BufferedReducerTestClass(1, 2));
		buffer.reduce(1, new BufferedReducerTestClass(1, 3));
		buffer.reduce(2, new BufferedReducerTestClass(2, 4));
		buffer.reduce(2, new BufferedReducerTestClass(2, 5));

		// Final map should still be empty before flush.
		Assertions.assertTrue(reduced.isEmpty());

		// Items should have been reduced after flush.
		buffer.flushLocalBuffer(reducer);
		Assertions.assertEquals(5, reduced.get(1).count);
		Assertions.assertEquals(9, reduced.get(2).count);

		// Reduces a few occurrences.
		buffer.reduce(1, new BufferedReducerTestClass(1, 6));
		buffer.reduce(1, new BufferedReducerTestClass(1, 7));
		buffer.reduce(2, new BufferedReducerTestClass(2, 8));
		buffer.reduce(2, new BufferedReducerTestClass(2, 9));

		// Items should have been reduced after flush.
		Assertions.assertEquals(5, reduced.get(1).count);
		Assertions.assertEquals(9, reduced.get(2).count);
		buffer.flushLocalBuffer(reducer);
		Assertions.assertEquals(18, reduced.get(1).count);
		Assertions.assertEquals(26, reduced.get(2).count);

	}
}
