package org.coldis.library.test.helper;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.helper.LocalPeriodicJobHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Periodic job helper test.
 */
public class PeriodicJobHelperTest {

	/**
	 * Sets up the test.
	 */
	@BeforeEach
	public void setUp() {
		LocalPeriodicJobHelper.clear("kind");
	}

	/**
	 * Tests if the job should run.
	 */
	@Test
	public void testShouldRun() throws Exception {
		final Duration wait = Duration.ofSeconds(2L);

		// Should run if the last run was not found.
		Assertions.assertTrue(LocalPeriodicJobHelper.shouldRun("kind", "value", wait, null));
		LocalPeriodicJobHelper.clearExpired();
		Assertions.assertNotNull(LocalPeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value"));
		Assertions.assertNotNull(LocalPeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getKey());
		Assertions.assertNotNull(LocalPeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getValue());

		// Should not run if duration has not passed.
		Assertions.assertFalse(LocalPeriodicJobHelper.shouldRun("kind", "value", wait, null));

		// Should run if duration has passed.
		Thread.sleep(wait.toMillis());
		Assertions.assertNotNull(LocalPeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value"));
		Assertions.assertNotNull(LocalPeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getKey());
		Assertions.assertNotNull(LocalPeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getValue());
		Assertions.assertTrue(LocalPeriodicJobHelper.shouldRun("kind", "value", wait, null));

		// Should run if duration has passed.
		Thread.sleep(wait.toMillis());
		LocalPeriodicJobHelper.clearExpired();
		Assertions.assertNull(LocalPeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value"));
		Assertions.assertTrue(LocalPeriodicJobHelper.shouldRun("kind", "value", wait, null));
	}

	/**
	 * Tests if the job should run with maximum wait unit.
	 */
	@Test
	public void testShouldRunWithMaximumWaitUnit() throws Exception {

		final Duration wait = Duration.ofDays(1L);
		final ChronoUnit maximumWaitUnit = ChronoUnit.DAYS;

		// Should run if the last run was not found.
		Assertions.assertTrue(LocalPeriodicJobHelper.shouldRun("kind", "value", wait, maximumWaitUnit));
		Assertions.assertTrue(
				LocalPeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getValue().isEqual(DateTimeHelper.getCurrentLocalDate().plusDays(1).atStartOfDay()));

	}

}
