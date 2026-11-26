package org.coldis.library.test.helper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.coldis.library.cache.LocalThrottle;
import org.coldis.library.cache.LocalThrottleEntry;
import org.coldis.library.cache.LocalThrottleReservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Local throttle test.
 */
public class LocalThrottleTest {

	/** Wait period. */
	private static final Duration WAIT_PERIOD = Duration.ofMinutes(1);

	/**
	 * Creates a throttle on a clock the test controls.
	 *
	 * @param  clock Clock.
	 * @return       The throttle.
	 */
	private LocalThrottle createThrottle(
			final AtomicReference<LocalDateTime> clock) {
		return new LocalThrottle(LocalThrottle.DEFAULT_TIMES_TO_RUN_BEFORE_CLEARING_EXPIRED, clock::get);
	}

	/**
	 * Tests that a key runs once per wait period, on the given clock.
	 */
	@Test
	@DisplayName("A key runs once per wait period, on the given clock")
	public void testAcquireOncePerWaitPeriod() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);

		final LocalThrottleReservation firstRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(firstRun);
		Assertions.assertEquals(clock.get(), firstRun.getAcquiredAt());
		Assertions.assertNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
		Assertions.assertNotNull(throttle.tryAcquire("other-key", LocalThrottleTest.WAIT_PERIOD, null));

		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		Assertions.assertNotNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Tests that a released run gives the key back.
	 */
	@Test
	@DisplayName("Releasing the run of a key that never ran gives the key back as new")
	public void testReleaseGivesTheKeyBack() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);

		final LocalThrottleReservation run = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(run);
		Assertions.assertTrue(throttle.release(run));
		Assertions.assertEquals(new LocalThrottleEntry(), throttle.getLastRun("key"));
		Assertions.assertNotNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
		Assertions.assertNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Tests that releasing a run does not undo a later run of the key.
	 */
	@Test
	@DisplayName("Releasing a run does not undo a later run of the key")
	public void testReleaseKeepsALaterRun() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);

		final LocalThrottleReservation oldRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		final LocalThrottleReservation laterRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(laterRun);

		Assertions.assertFalse(throttle.release(oldRun));
		Assertions.assertEquals(laterRun.getAcquiredAt(), throttle.getLastRun("key").getLastRunAt());
		Assertions.assertNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Tests that the expired entries are cleared on the given clock.
	 */
	@Test
	@DisplayName("Expired entries are cleared on the given clock")
	public void testClearExpired() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);
		throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);

		throttle.clearExpired();
		Assertions.assertEquals(clock.get(), throttle.getLastRun("key").getLastRunAt());

		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		throttle.clearExpired();
		Assertions.assertEquals(LocalDateTime.MIN, throttle.getLastRun("key").getLastRunAt());
	}

	/**
	 * Tests that releasing a run restores the key's earlier run, so the key waits
	 * (or not) as it did before.
	 */
	@Test
	@DisplayName("Releasing a run restores the key's earlier run")
	public void testReleaseRestoresTheEarlierRun() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);
		final LocalThrottleReservation firstRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(firstRun);

		// A second run after the wait period, released: the first run is back, and its wait period is over.
		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		final LocalThrottleReservation secondRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(secondRun);
		Assertions.assertTrue(throttle.release(secondRun));
		Assertions.assertEquals(firstRun.getAcquiredAt(), throttle.getLastRun("key").getLastRunAt());
		Assertions.assertEquals(firstRun.getNextRunAfter(), throttle.getLastRun("key").getNextRunAfter());
		Assertions.assertNotNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Tests that releasing a next-day run restores the previous day's run (once a
	 * day), after which the key can run again.
	 */
	@Test
	@DisplayName("Releasing a next-day run restores the previous day's run, and the key can run again")
	public void testReleaseRestoresThePreviousDayRun() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);
		final Duration day = Duration.ofDays(1);
		Assertions.assertNotNull(throttle.tryAcquire("key", day, ChronoUnit.DAYS));

		// The next day the key runs again; that run is released, so the first day's run is back.
		clock.set(LocalDateTime.of(2026, 1, 2, 0, 0, 1));
		final LocalThrottleReservation nextDayRun = throttle.tryAcquire("key", day, ChronoUnit.DAYS);
		Assertions.assertNotNull(nextDayRun);
		Assertions.assertTrue(throttle.release(nextDayRun));
		Assertions.assertEquals(LocalDateTime.of(2026, 1, 2, 0, 0), throttle.getLastRun("key").getNextRunAfter());
		Assertions.assertNotNull(throttle.tryAcquire("key", day, ChronoUnit.DAYS));
		Assertions.assertNull(throttle.tryAcquire("key", day, ChronoUnit.DAYS));
	}

	/**
	 * Tests the once-per-unit limit on the given clock: a key that ran just before
	 * midnight runs again right after it.
	 */
	@Test
	@DisplayName("With a day as maximum wait unit, a key runs again once the day turns")
	public void testMaximumWaitUnit() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 23, 59));
		final LocalThrottle throttle = this.createThrottle(clock);
		final Duration day = Duration.ofDays(1);

		final LocalThrottleReservation run = throttle.tryAcquire("key", day, ChronoUnit.DAYS);
		Assertions.assertNotNull(run);
		Assertions.assertEquals(LocalDateTime.of(2026, 1, 2, 0, 0), run.getNextRunAfter());
		Assertions.assertNull(throttle.tryAcquire("key", day, ChronoUnit.DAYS));

		clock.set(LocalDateTime.of(2026, 1, 2, 0, 0, 1));
		Assertions.assertNotNull(throttle.tryAcquire("key", day, ChronoUnit.DAYS));
	}

	/**
	 * Tests that a restored run still inside the wait blocks the key.
	 */
	@Test
	@DisplayName("A restored run still inside the wait blocks the key")
	public void testRestoredRunInsideTheWaitBlocks() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);
		Assertions.assertNotNull(throttle.tryAcquire("key", Duration.ofMinutes(1), null));
		clock.set(LocalDateTime.of(2026, 1, 1, 10, 2));
		final LocalThrottleReservation laterRun = throttle.tryAcquire("key", Duration.ofMinutes(1), null);
		Assertions.assertNotNull(laterRun);

		Assertions.assertTrue(throttle.release(laterRun));
		Assertions.assertEquals(LocalDateTime.of(2026, 1, 1, 10, 0), throttle.getLastRun("key").getLastRunAt());
		Assertions.assertNull(throttle.tryAcquire("key", Duration.ofMinutes(5), null));
	}

	/**
	 * Tests that releasing a run after the entries were cleared does not bring an
	 * earlier run back.
	 */
	@Test
	@DisplayName("Releasing a run after the entries were cleared does not bring an earlier run back")
	public void testReleaseAfterClear() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);
		Assertions.assertNotNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		final LocalThrottleReservation laterRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(laterRun);

		throttle.clear();
		Assertions.assertFalse(throttle.release(laterRun));
		Assertions.assertEquals(new LocalThrottleEntry(), throttle.getLastRun("key"));
	}

	/**
	 * Tests that a stale reservation does not undo a run acquired after a clear at
	 * the same instant.
	 */
	@Test
	@DisplayName("A reservation from before a clear does not undo a run acquired after it at the same instant")
	public void testStaleReservationAfterClearKeepsTheNewRun() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);
		final LocalThrottleReservation staleRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(staleRun);
		throttle.clear();
		final LocalThrottleReservation newRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(newRun);

		Assertions.assertFalse(throttle.release(staleRun));
		Assertions.assertEquals(newRun.getAcquiredAt(), throttle.getLastRun("key").getLastRunAt());
		Assertions.assertNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Tests that a reservation releases its run only once: a second release does
	 * not undo a run acquired at the same instant after the first.
	 */
	@Test
	@DisplayName("A reservation releases its run only once")
	public void testReleaseIsSingleUse() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);
		final LocalThrottleReservation firstRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(firstRun);
		Assertions.assertTrue(throttle.release(firstRun));
		Assertions.assertFalse(throttle.release(firstRun));
		final LocalThrottleReservation secondRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(secondRun);

		Assertions.assertFalse(throttle.release(firstRun));
		Assertions.assertEquals(secondRun.getAcquiredAt(), throttle.getLastRun("key").getLastRunAt());
		Assertions.assertNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Waits until a key's entry is cleared (by the background clear).
	 *
	 * @param  throttle             Throttle.
	 * @param  key                  Key.
	 * @return                      If the entry was cleared in time.
	 * @throws InterruptedException If interrupted while waiting.
	 */
	private boolean waitUntilCleared(
			final LocalThrottle throttle,
			final String key) throws InterruptedException {
		boolean cleared = new LocalThrottleEntry().equals(throttle.getLastRun(key));
		for (int attempt = 0; (attempt < 100) && !cleared; attempt++) {
			Thread.sleep(50);
			cleared = new LocalThrottleEntry().equals(throttle.getLastRun(key));
		}
		return cleared;
	}

	/**
	 * Tests that the expired entries are cleared in the background each time the
	 * runs cross the threshold.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("Expired entries are cleared in the background each time the runs cross the threshold")
	public void testBackgroundClearOnThreshold() throws Exception {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = new LocalThrottle(3L, clock::get);

		// Run 1 records the key; once expired, runs 2 and 3 (other keys) cross the threshold.
		throttle.tryAcquire("expiring-key", LocalThrottleTest.WAIT_PERIOD, null);
		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		throttle.tryAcquire("other-key-1", LocalThrottleTest.WAIT_PERIOD, null);
		throttle.tryAcquire("other-key-2", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertTrue(this.waitUntilCleared(throttle, "expiring-key"));

		// The counter starts again: three more runs clear the next expired key.
		throttle.tryAcquire("expiring-key-2", LocalThrottleTest.WAIT_PERIOD, null);
		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		throttle.tryAcquire("other-key-3", LocalThrottleTest.WAIT_PERIOD, null);
		throttle.tryAcquire("other-key-4", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertTrue(this.waitUntilCleared(throttle, "expiring-key-2"));
	}

	/**
	 * Tests that a threshold below 1 clears the expired entries on every run.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("A threshold below 1 clears the expired entries on every run")
	public void testThresholdBelowOneClearsOnEveryRun() throws Exception {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = new LocalThrottle(0L, clock::get);
		throttle.tryAcquire("expiring-key", LocalThrottleTest.WAIT_PERIOD, null);
		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		throttle.tryAcquire("other-key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertTrue(this.waitUntilCleared(throttle, "expiring-key"));
	}

	/**
	 * Throttle that counts its clears of the expired entries.
	 */
	private static class ClearCountingThrottle extends LocalThrottle {

		/** Clears. */
		private final AtomicInteger clears = new AtomicInteger();

		/**
		 * Creates the throttle.
		 *
		 * @param timesToRunBeforeClearingExpired Number of runs before clearing.
		 * @param clock                           Clock.
		 */
		ClearCountingThrottle(final Long timesToRunBeforeClearingExpired, final AtomicReference<LocalDateTime> clock) {
			super(timesToRunBeforeClearingExpired, clock::get);
		}

		/**
		 * @see org.coldis.library.cache.LocalThrottle#clearExpired()
		 */
		@Override
		public void clearExpired() {
			super.clearExpired();
			this.clears.incrementAndGet();
		}

	}

	/**
	 * Waits until a throttle cleared a number of times, then a little longer, and
	 * gets its clears.
	 *
	 * @param  throttle             Throttle.
	 * @param  expectedClears       Clears waited for.
	 * @return                      The clears.
	 * @throws InterruptedException If interrupted while waiting.
	 */
	private int waitForClears(
			final ClearCountingThrottle throttle,
			final int expectedClears) throws InterruptedException {
		for (int attempt = 0; (attempt < 100) && (throttle.clears.get() < expectedClears); attempt++) {
			Thread.sleep(50);
		}
		Thread.sleep(200);
		return throttle.clears.get();
	}

	/**
	 * Tests that each crossing of the threshold queues exactly one clear, and that
	 * a threshold below 1 clears after the very first run.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("Each crossing of the threshold queues exactly one clear, and a threshold below 1 clears after every run")
	public void testClearsQueuedOncePerCrossing() throws Exception {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final ClearCountingThrottle throttle = new ClearCountingThrottle(3L, clock);
		for (int run = 1; run <= 3; run++) {
			throttle.tryAcquire("key-" + run, LocalThrottleTest.WAIT_PERIOD, null);
		}
		Assertions.assertEquals(1, this.waitForClears(throttle, 1));
		for (int run = 4; run <= 6; run++) {
			throttle.tryAcquire("key-" + run, LocalThrottleTest.WAIT_PERIOD, null);
		}
		Assertions.assertEquals(2, this.waitForClears(throttle, 2));

		final ClearCountingThrottle everyRunThrottle = new ClearCountingThrottle(0L, clock);
		everyRunThrottle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertEquals(1, this.waitForClears(everyRunThrottle, 1));
	}

}
