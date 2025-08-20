package org.coldis.library.test.model;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.model.RetryBackoff;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Retry backoff test class.
 */
public class RetryBackoffTest {

	/** The clock to use for the tests. */
	private static final Clock CLOCK = DateTimeHelper.getClock();

	class RetryOwner {

		public static Boolean SHOULD_RETRY = true;

		public static LocalDateTime BASE_DATE = DateTimeHelper.getCurrentLocalDateTime().truncatedTo(ChronoUnit.MINUTES);

		public RetryOwner() {
		}

		public Boolean getShouldRetry() {
			return RetryOwner.SHOULD_RETRY;
		}

		public LocalDateTime getBaseDate() {
			return RetryOwner.BASE_DATE;
		}
	}

	/**
	 * Sets the clock to use for the tests.
	 */
	@BeforeEach
	@AfterEach
	public void setUp() {
		DateTimeHelper.setClock(RetryBackoffTest.CLOCK);
	}

	/**
	 * Tests the retry backoff.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testRetryBackoff() throws Exception {

		// Test objects.
		final RetryOwner retryOwner = new RetryOwner();
		final RetryBackoff<RetryOwner> retryBackoff = new RetryBackoff<>(retryOwner, RetryOwner::getShouldRetry, RetryOwner::getBaseDate);

		// Check initial values.
		Assertions.assertEquals(0, retryBackoff.getAttempts());
		final LocalDateTime firstAttemptAt = retryBackoff.getNextAttemptAt();
		DateTimeHelper.adjustClock(Duration.ofMinutes(1));
		Assertions.assertEquals(firstAttemptAt, retryBackoff.getNextAttemptAt());
		Assertions.assertEquals(0, retryBackoff.getAttempts());

		// Check first retry.
		retryBackoff.incrementRetries();
		Assertions.assertTrue(retryBackoff.shouldRetry());
		Assertions.assertEquals(1, retryBackoff.getAttempts());
		Assertions.assertEquals(firstAttemptAt, retryBackoff.getNextAttemptAt());

		// Check second retry.
		retryBackoff.incrementRetries();
		Assertions.assertTrue(retryBackoff.shouldRetry());
		Assertions.assertEquals(2, retryBackoff.getAttempts());
		Assertions.assertEquals(firstAttemptAt.plusMinutes(2), retryBackoff.getNextAttemptAt());

		// Check third retry.
		retryBackoff.incrementRetries();
		Assertions.assertTrue(retryBackoff.shouldRetry());
		Assertions.assertEquals(3, retryBackoff.getAttempts());
		Assertions.assertEquals(firstAttemptAt.plus(Double.valueOf(2 * 60 * 1.5 * 1000).longValue(), ChronoUnit.MILLIS), retryBackoff.getNextAttemptAt());

		// Check forth retry.
		retryBackoff.incrementRetries();
		Assertions.assertTrue(retryBackoff.shouldRetry());
		Assertions.assertEquals(4, retryBackoff.getAttempts());
		Assertions.assertEquals(firstAttemptAt.plus(Double.valueOf(2 * 60 * 1.5 * 1.5 * 1000).longValue(), ChronoUnit.MILLIS), retryBackoff.getNextAttemptAt());

	}

}
