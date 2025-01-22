package org.coldis.library.test.helper;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.helper.PeriodicJobHelper;
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
		PeriodicJobHelper.clear("kind");
	}

	/**
	 * Tests if the job should run.
	 */
	@Test
	public void testShouldRun() throws Exception {
		final Duration wait = Duration.ofSeconds(2L);

		// Should run if the last run was not found.
		Assertions.assertTrue(PeriodicJobHelper.shouldRun("kind", "value", wait, null));
		PeriodicJobHelper.clearExpired();
		Assertions.assertNotNull(PeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value"));
		Assertions.assertNotNull(PeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getKey());
		Assertions.assertNotNull(PeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getValue());

		// Should not run if duration has not passed.
		Assertions.assertFalse(PeriodicJobHelper.shouldRun("kind", "value", wait, null));

		// Should run if duration has passed.
		Thread.sleep(wait.toMillis());
		Assertions.assertNotNull(PeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value"));
		Assertions.assertNotNull(PeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getKey());
		Assertions.assertNotNull(PeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getValue());
		Assertions.assertTrue(PeriodicJobHelper.shouldRun("kind", "value", wait, null));

		// Should run if duration has passed.
		Thread.sleep(wait.toMillis());
		PeriodicJobHelper.clearExpired();
		Assertions.assertNull(PeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value"));
		Assertions.assertTrue(PeriodicJobHelper.shouldRun("kind", "value", wait, null));
	}

	/**
	 * Tests if the job should run with maximum wait unit.
	 */
	@Test
	public void testShouldRunWithMaximumWaitUnit() throws Exception {

		final Duration wait = Duration.ofDays(1L);
		final ChronoUnit maximumWaitUnit = ChronoUnit.DAYS;

		// Should run if the last run was not found.
		Assertions.assertTrue(PeriodicJobHelper.shouldRun("kind", "value", wait, maximumWaitUnit));
		Assertions.assertTrue(
				PeriodicJobHelper.LAST_JOB_RUNS.get("kind").get("value").getValue().isEqual(DateTimeHelper.getCurrentLocalDate().plusDays(1).atStartOfDay()));

	}

}
