package org.coldis.library.test.model;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.TreeSet;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.model.RetryBackoff;
import org.coldis.library.model.view.ModelView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
	@DisplayName("Serializing while not retrying keeps the stored state")
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
	@DisplayName("A deserialized backoff keeps its state until its owner is wired")
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
	@DisplayName("The scheduling check resets once retrying is over, and a new cycle starts clean")
	public void testBusinessGettersResetOnceRetryingIsOver() throws Exception {
		final RetryBackoff<RetryOwner> retryBackoff = RetryBackoffTest.buildRetryingBackoff(new RetryOwner());
		Assertions.assertFalse(retryBackoff.getShouldScheduleNextAttempt());

		// The owner's update after the retry is over checks it, and it resets. The state
		// is observed through serialization, which no longer changes anything.
		RetryOwner.SHOULD_RETRY = false;
		Assertions.assertFalse(retryBackoff.getShouldScheduleNextAttempt());
		final JsonNode json = RetryBackoffTest.OBJECT_MAPPER.readTree(
				RetryBackoffTest.OBJECT_MAPPER.writerWithView(ModelView.Persistent.class).writeValueAsString(retryBackoff));
		RetryOwner.SHOULD_RETRY = true;
		Assertions.assertEquals(0, json.get("attempts").asInt());
		Assertions.assertTrue(json.get("nextAttemptAt").isNull());
		Assertions.assertTrue(json.get("nextAttemptScheduledFor").isNull());

		// A new retry cycle starts from the first attempt, at the base date, and
		// schedules.
		Assertions.assertTrue(retryBackoff.getShouldScheduleNextAttempt());
		Assertions.assertEquals(0, retryBackoff.getAttempts());
		Assertions.assertEquals(RetryOwner.BASE_DATE, retryBackoff.getNextAttemptAt());
	}

	/**
	 * The serialized computed values match the business getters while retrying.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("The serialized computed values match the getters while retrying")
	public void testSerializedComputedValuesMatchTheGetters() throws Exception {
		final RetryBackoff<RetryOwner> retryBackoff = new RetryBackoff<>(new RetryOwner(), RetryOwner::getShouldRetry, RetryOwner::getBaseDate);
		retryBackoff.incrementRetries();
		retryBackoff.incrementRetries();
		final JsonNode json = RetryBackoffTest.OBJECT_MAPPER.readTree(
				RetryBackoffTest.OBJECT_MAPPER.writerWithView(ModelView.Persistent.class).writeValueAsString(retryBackoff));
		Assertions.assertEquals(retryBackoff.getShouldScheduleNextAttempt(), json.get("shouldScheduleNextAttempt").asBoolean());
		Assertions.assertEquals(retryBackoff.getNextAttemptDelay(), RetryBackoffTest.OBJECT_MAPPER.treeToValue(json.get("nextAttemptDelay"), Duration.class));
		Assertions.assertEquals(retryBackoff.getAttempts(), json.get("attempts").asInt());
	}

	/**
	 * The retries stop once past the maximum.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("Retries stop past the maximum")
	public void testGivesUpAfterMaxRetries() throws Exception {
		final RetryBackoff<RetryOwner> retryBackoff = new RetryBackoff<>(new RetryOwner(), RetryOwner::getShouldRetry, RetryOwner::getBaseDate);
		retryBackoff.setMaxRetries(3);
		retryBackoff.incrementRetries();
		retryBackoff.incrementRetries();
		retryBackoff.incrementRetries();
		// At exactly the maximum, there is still a next attempt.
		Assertions.assertEquals(3, retryBackoff.getAttempts());
		Assertions.assertNotNull(retryBackoff.getNextAttemptAt());
		Assertions.assertTrue(retryBackoff.getShouldScheduleNextAttempt());
		// One more and the retries stop.
		retryBackoff.incrementRetries();
		Assertions.assertEquals(4, retryBackoff.getAttempts());
		Assertions.assertNull(retryBackoff.getNextAttemptAt());
		Assertions.assertFalse(retryBackoff.getShouldScheduleNextAttempt());
	}

	/**
	 * JSON stored before the serialization change reads back with all its state.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("JSON stored by the previous version reads back with its state")
	public void testReadsPreviouslyStoredJson() throws Exception {
		final String storedJson = "{\"backoff\":120.000000000,\"backoffMultiplier\":1.5,\"maxBackoff\":7200.000000000,\"maxRetries\":21,"
				+ "\"attempts\":3,\"nextAttemptAt\":\"2026-11-27T10:03:00\",\"nextAttemptScheduledFor\":\"2026-11-27T10:02:00\","
				+ "\"typeName\":\"org.coldis.library.model.RetryBackoff\",\"nextAttemptDelay\":null,\"shouldScheduleNextAttempt\":true}";
		@SuppressWarnings("unchecked")
		final RetryBackoff<RetryOwner> readBackoff = RetryBackoffTest.OBJECT_MAPPER.readValue(storedJson, RetryBackoff.class);
		readBackoff.setRetryOwner(new RetryOwner());
		readBackoff.setShouldRetry(RetryOwner::getShouldRetry);
		readBackoff.setBaseDateFunction(RetryOwner::getBaseDate);

		Assertions.assertEquals(3, readBackoff.getAttempts());
		Assertions.assertEquals(LocalDateTime.parse("2026-11-27T10:03:00"), readBackoff.getNextAttemptAt());
		Assertions.assertEquals(LocalDateTime.parse("2026-11-27T10:02:00"), readBackoff.getNextAttemptScheduledFor());
	}

	/**
	 * An unlimited max retries ({@link Integer#MAX_VALUE}), as stored before, is
	 * written capped in both views and still allows retrying after reading back.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("Integer.MAX_VALUE max retries is never written out, and reads back as unlimited")
	public void testUnlimitedMaxRetriesIsWrittenCapped() throws Exception {
		final String storedJson = "{\"backoff\":5.000000000,\"backoffMultiplier\":1.5,\"maxBackoff\":7200.000000000,\"maxRetries\":2147483647,"
				+ "\"attempts\":0,\"typeName\":\"org.coldis.library.model.RetryBackoff\",\"shouldScheduleNextAttempt\":false}";
		@SuppressWarnings("unchecked")
		final RetryBackoff<RetryOwner> readBackoff = RetryBackoffTest.OBJECT_MAPPER.readValue(storedJson, RetryBackoff.class);
		Assertions.assertEquals(RetryBackoff.MAX_SERIALIZED_MAX_RETRIES, readBackoff.getMaxRetries());
		for (final Class<?> view : Set.of(ModelView.Persistent.class, ModelView.Public.class)) {
			final String written = RetryBackoffTest.OBJECT_MAPPER.writerWithView(view).writeValueAsString(readBackoff);
			Assertions.assertFalse(written.contains(String.valueOf(Integer.MAX_VALUE)), view.getSimpleName() + ": " + written);
			Assertions.assertEquals(RetryBackoff.MAX_SERIALIZED_MAX_RETRIES.intValue(),
					RetryBackoffTest.OBJECT_MAPPER.readTree(written).get("maxRetries").intValue(), view.getSimpleName());
		}

		// Reading the capped value back keeps retrying well past the default limit.
		@SuppressWarnings("unchecked")
		final RetryBackoff<RetryOwner> roundTrip = RetryBackoffTest.OBJECT_MAPPER
				.readValue(RetryBackoffTest.OBJECT_MAPPER.writeValueAsString(readBackoff), RetryBackoff.class);
		roundTrip.setRetryOwner(new RetryOwner());
		roundTrip.setShouldRetry(RetryOwner::getShouldRetry);
		roundTrip.setBaseDateFunction(RetryOwner::getBaseDate);
		for (int attempt = 0; attempt < (RetryBackoff.DEFAULT_MAX_RETRIES * 2); attempt++) {
			roundTrip.incrementRetries();
		}
		Assertions.assertTrue(roundTrip.getShouldScheduleNextAttempt());
	}

	/**
	 * A limit under the cap is written as is.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("A max retries under the cap is written unchanged")
	public void testRegularMaxRetriesIsWrittenUnchanged() throws Exception {
		final RetryBackoff<RetryOwner> retryBackoff = RetryBackoffTest.buildRetryingBackoff(new RetryOwner());
		Assertions.assertEquals(RetryBackoff.DEFAULT_MAX_RETRIES.intValue(),
				RetryBackoffTest.OBJECT_MAPPER.readTree(RetryBackoffTest.OBJECT_MAPPER.writeValueAsString(retryBackoff)).get("maxRetries").intValue());
	}

	/**
	 * The persistent and public views write the same keys as before the change.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("Both views write the same JSON keys as before")
	public void testSerializedKeysAreUnchanged() throws Exception {
		final Set<String> expectedKeys = new TreeSet<>(Set.of("backoff", "backoffMultiplier", "maxBackoff", "maxRetries", "attempts", "nextAttemptAt",
				"nextAttemptScheduledFor", "typeName", "nextAttemptDelay", "shouldScheduleNextAttempt"));
		final RetryBackoff<RetryOwner> retryBackoff = RetryBackoffTest.buildRetryingBackoff(new RetryOwner());
		for (final Class<?> view : Set.of(ModelView.Persistent.class, ModelView.Public.class)) {
			final Set<String> keys = new TreeSet<>();
			RetryBackoffTest.OBJECT_MAPPER.readTree(RetryBackoffTest.OBJECT_MAPPER.writerWithView(view).writeValueAsString(retryBackoff)).fieldNames()
					.forEachRemaining(keys::add);
			Assertions.assertEquals(expectedKeys, keys, view.getSimpleName());
		}
	}

}
