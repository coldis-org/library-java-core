package org.coldis.library.test.helper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
		throttle.release(run);
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

		throttle.release(oldRun);
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
		throttle.release(secondRun);
		Assertions.assertEquals(firstRun.getAcquiredAt(), throttle.getLastRun("key").getLastRunAt());
		Assertions.assertEquals(firstRun.getNextRunAfter(), throttle.getLastRun("key").getNextRunAfter());
		Assertions.assertNotNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Tests that a restored earlier run still blocks the key while its wait is not
	 * over (once a day).
	 */
	@Test
	@DisplayName("A restored earlier run still blocks the key until its next run")
	public void testReleaseRestoresAnEarlierRunStillWaiting() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);
		final Duration day = Duration.ofDays(1);
		Assertions.assertNotNull(throttle.tryAcquire("key", day, ChronoUnit.DAYS));

		// The next day the key runs again; that run is released, so the first day's run is back.
		clock.set(LocalDateTime.of(2026, 1, 2, 0, 0, 1));
		final LocalThrottleReservation nextDayRun = throttle.tryAcquire("key", day, ChronoUnit.DAYS);
		Assertions.assertNotNull(nextDayRun);
		throttle.release(nextDayRun);
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
	 * Tests that releasing a run of a cleared entry does not bring the entry back.
	 */
	@Test
	@DisplayName("Releasing a run after the entries were cleared does not bring the run back")
	public void testReleaseAfterClear() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);
		final LocalThrottleReservation run = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(run);

		throttle.clear();
		throttle.release(run);
		Assertions.assertEquals(new LocalThrottleEntry(), throttle.getLastRun("key"));
	}

}
