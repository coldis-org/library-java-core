package org.coldis.library.test.model;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.model.RetryBackoff;
import org.coldis.library.model.view.ModelView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
		RetryOwner.SHOULD_RETRY = true;
	}

	/** Object mapper configured like a persistence converter. */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	/**
	 * Builds a backoff that retried three times and has its next attempt scheduled.
	 *
	 * @param  retryOwner The retry owner.
	 * @return            The backoff.
	 */
	private static RetryBackoff<RetryOwner> buildRetryingBackoff(
			final RetryOwner retryOwner) {
		final RetryBackoff<RetryOwner> retryBackoff = new RetryBackoff<>(retryOwner, RetryOwner::getShouldRetry, RetryOwner::getBaseDate);
		retryBackoff.incrementRetries();
		retryBackoff.incrementRetries();
		retryBackoff.incrementRetries();
		retryBackoff.setScheduled();
		return retryBackoff;
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

	/**
	 * Serializing while the predicate is (transiently) false, as a persistence
	 * snapshot does before the owner is wired, must not change the stored state.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testSerializationKeepsTheStateWhileShouldRetryIsFalse() throws Exception {
		final RetryBackoff<RetryOwner> retryBackoff = RetryBackoffTest.buildRetryingBackoff(new RetryOwner());
		final LocalDateTime nextAttemptAt = retryBackoff.getNextAttemptAt();
		final LocalDateTime scheduledFor = retryBackoff.getNextAttemptScheduledFor();
		Assertions.assertEquals(3, retryBackoff.getAttempts());

		RetryOwner.SHOULD_RETRY = false;
		final JsonNode json = RetryBackoffTest.OBJECT_MAPPER.readTree(
				RetryBackoffTest.OBJECT_MAPPER.writerWithView(ModelView.Persistent.class).writeValueAsString(retryBackoff));
		RetryOwner.SHOULD_RETRY = true;

		Assertions.assertEquals(3, json.get("attempts").asInt());
		Assertions.assertFalse(json.get("nextAttemptScheduledFor").isNull());
		Assertions.assertFalse(json.get("shouldScheduleNextAttempt").asBoolean());
		Assertions.assertEquals(3, retryBackoff.getAttempts());
		Assertions.assertEquals(nextAttemptAt, retryBackoff.getNextAttemptAt());
		Assertions.assertEquals(scheduledFor, retryBackoff.getNextAttemptScheduledFor());
	}

	/**
	 * A backoff read back from JSON keeps its state until its owner is wired, even
	 * though the no-args predicate says it is not retrying.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testDeserializedStateSurvivesUntilTheOwnerIsWired() throws Exception {
		final RetryOwner retryOwner = new RetryOwner();
		final RetryBackoff<RetryOwner> retryBackoff = RetryBackoffTest.buildRetryingBackoff(retryOwner);
		final String json = RetryBackoffTest.OBJECT_MAPPER.writerWithView(ModelView.Persistent.class).writeValueAsString(retryBackoff);

		@SuppressWarnings("unchecked")
		final RetryBackoff<RetryOwner> readBackoff = RetryBackoffTest.OBJECT_MAPPER.readValue(json, RetryBackoff.class);
		// Serialized again before the owner is wired, as a persistence snapshot does.
		RetryBackoffTest.OBJECT_MAPPER.writerWithView(ModelView.Persistent.class).writeValueAsString(readBackoff);
		readBackoff.setRetryOwner(retryOwner);
		readBackoff.setShouldRetry(RetryOwner::getShouldRetry);
		readBackoff.setBaseDateFunction(RetryOwner::getBaseDate);

		Assertions.assertEquals(retryBackoff, readBackoff);
		Assertions.assertEquals(3, readBackoff.getAttempts());
		Assertions.assertEquals(retryBackoff.getNextAttemptScheduledFor(), readBackoff.getNextAttemptScheduledFor());
		Assertions.assertFalse(readBackoff.getShouldScheduleNextAttempt());
	}

	/**
	 * The business getters still reset the state once retrying is over, so a later
	 * retry cycle starts clean and schedules again.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testBusinessGettersResetOnceRetryingIsOver() throws Exception {
		final RetryBackoff<RetryOwner> retryBackoff = RetryBackoffTest.buildRetryingBackoff(new RetryOwner());
		Assertions.assertFalse(retryBackoff.getShouldScheduleNextAttempt());

		// The owner's update after the retry is over checks it, and it resets.
		RetryOwner.SHOULD_RETRY = false;
		Assertions.assertFalse(retryBackoff.getShouldScheduleNextAttempt());
		Assertions.assertNull(retryBackoff.getNextAttemptScheduledFor());
		RetryOwner.SHOULD_RETRY = true;

		// A new retry cycle starts from the first attempt and schedules.
		Assertions.assertEquals(0, retryBackoff.getAttempts());
		Assertions.assertNull(retryBackoff.getNextAttemptScheduledFor());
		Assertions.assertTrue(retryBackoff.getShouldScheduleNextAttempt());
	}

	/**
	 * The serialized computed values match the business getters while retrying.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testSerializedComputedValuesMatchTheGetters() throws Exception {
		final RetryBackoff<RetryOwner> retryBackoff = new RetryBackoff<>(new RetryOwner(), RetryOwner::getShouldRetry, RetryOwner::getBaseDate);
		retryBackoff.incrementRetries();
		retryBackoff.incrementRetries();
		final JsonNode json = RetryBackoffTest.OBJECT_MAPPER.readTree(
				RetryBackoffTest.OBJECT_MAPPER.writerWithView(ModelView.Persistent.class).writeValueAsString(retryBackoff));
		Assertions.assertEquals(retryBackoff.getShouldScheduleNextAttempt(), json.get("shouldScheduleNextAttempt").asBoolean());
		Assertions.assertEquals(retryBackoff.getAttempts(), json.get("attempts").asInt());
	}

	/**
	 * The retries stop once past the maximum.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testGivesUpAfterMaxRetries() throws Exception {
		final RetryBackoff<RetryOwner> retryBackoff = new RetryBackoff<>(new RetryOwner(), RetryOwner::getShouldRetry, RetryOwner::getBaseDate);
		retryBackoff.setMaxRetries(3);
		for (int attempt = 0; attempt < 5; attempt++) {
			retryBackoff.incrementRetries();
		}
		Assertions.assertTrue(retryBackoff.getAttempts() > retryBackoff.getMaxRetries());
		Assertions.assertNull(retryBackoff.getNextAttemptAt());
		Assertions.assertFalse(retryBackoff.getShouldScheduleNextAttempt());
	}

}
