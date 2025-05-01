package org.coldis.library.test.helper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import org.coldis.library.cache.LocalPeriodicJobCache;
import org.coldis.library.cache.LocalPeriodicJobCacheEntry;
import org.coldis.library.helper.DateTimeHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Periodic job helper test.
 */
public class PeriodicJobHelperTest {

	/**
	 * Tests if the job should run.
	 */
	@Test
	public void testShouldRun() throws Exception {

		final LocalPeriodicJobCache jobCache = new LocalPeriodicJobCache();
		final Duration wait = Duration.ofSeconds(2L);

		// Should run if the last run was not found.
		Assertions.assertTrue(jobCache.shouldRun("value", wait, null));
		jobCache.clearExpired();
		Assertions.assertNotNull(jobCache.getLastRun("value"));
		Assertions.assertTrue(jobCache.getLastRun("value").getLastRunAt().isAfter(LocalDateTime.MIN));
		Assertions.assertTrue(jobCache.getLastRun("value").getNextRunAfter().isAfter(LocalDateTime.MIN));

		// Should not run if duration has not passed.
		Assertions.assertFalse(jobCache.shouldRun("value", wait, null));

		// Should run if duration has passed.
		Thread.sleep(wait.toMillis());
		Assertions.assertNotNull(jobCache.getLastRun("value"));
		Assertions.assertTrue(jobCache.getLastRun("value").getLastRunAt().isAfter(LocalDateTime.MIN));
		Assertions.assertTrue(jobCache.getLastRun("value").getNextRunAfter().isAfter(LocalDateTime.MIN));
		Assertions.assertTrue(jobCache.shouldRun("value", wait, null));

		// Should run if duration has passed.
		Thread.sleep(wait.toMillis());
		jobCache.clearExpired();
		Assertions.assertEquals(new LocalPeriodicJobCacheEntry(), jobCache.getLastRun("value"));
		Assertions.assertTrue(jobCache.shouldRun("value", wait, null));
	}

	/**
	 * Tests if the job should run with a wait unit.
	 */
	private class TestJob implements Runnable {

		/** Job cache. */
		private final LocalPeriodicJobCache jobCache;

		/** Wait. */
		private final Duration wait;

		/** Count. */
		private int count;

		/**
		 * Creates a new instance of the class.
		 *
		 * @param jobCache Job cache.
		 * @param wait     Wait.
		 */
		public TestJob(final LocalPeriodicJobCache jobCache, final Duration wait) {
			this.jobCache = jobCache;
			this.wait = wait;
		}

		@Override
		public void run() {
			if (this.jobCache.shouldRun("value", this.wait, null)) {
				this.count++;
			}
		}

	}

	/**
	 * Tests if the job run with parallel execution.
	 */
	@Test
	public void testShouldRunWithParallelExecution() throws Exception {

		final LocalPeriodicJobCache jobCache = new LocalPeriodicJobCache();
		final Duration wait = Duration.ofSeconds(2L);

		// Run some tasks in parallel.
		final TestJob job = new TestJob(jobCache, wait);
		final CompletableFuture<Void> run1 = CompletableFuture.runAsync(job);
		final CompletableFuture<Void> run2 = CompletableFuture.runAsync(job);
		final CompletableFuture<Void> run3 = CompletableFuture.runAsync(job);
		final CompletableFuture<Void> run4 = CompletableFuture.runAsync(job);
		final CompletableFuture<Void> run5 = CompletableFuture.runAsync(job);
		final CompletableFuture<Void> run6 = CompletableFuture.runAsync(job);
		final CompletableFuture<Void> run7 = CompletableFuture.runAsync(job);
		CompletableFuture.allOf(run1, run2, run3, run4, run5, run6, run7).join();

		// Assert that the job was executed only once.
		Assertions.assertTrue(jobCache.getLastRun("value").getLastRunAt().isAfter(LocalDateTime.MIN));
		Assertions.assertTrue(jobCache.getLastRun("value").getNextRunAfter().isAfter(LocalDateTime.MIN));
		Assertions.assertEquals(1, job.count);

	}

	/**
	 * Tests if the job should run with maximum wait unit.
	 */
	@Test
	public void testShouldRunWithMaximumWaitUnit() throws Exception {

		final LocalPeriodicJobCache jobCache = new LocalPeriodicJobCache();
		final Duration wait = Duration.ofDays(1L);
		final ChronoUnit maximumWaitUnit = ChronoUnit.DAYS;

		// Should run if the last run was not found.
		Assertions.assertTrue(jobCache.shouldRun("value", wait, maximumWaitUnit));
		Assertions.assertTrue(jobCache.getLastRun("value").getNextRunAfter().isEqual(DateTimeHelper.getCurrentLocalDate().plusDays(1).atStartOfDay()));

	}

}
