package org.coldis.library.test.helper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import org.coldis.library.cache.LocalThrottle;
import org.coldis.library.cache.LocalThrottleReservation;
import org.junit.jupiter.api.Assertions;
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
	public void testAcquireOncePerWaitPeriod() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);

		final LocalThrottleReservation firstRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(firstRun);
		Assertions.assertEquals(clock.get(), firstRun.acquiredAt());
		Assertions.assertNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
		Assertions.assertNotNull(throttle.tryAcquire("other-key", LocalThrottleTest.WAIT_PERIOD, null));

		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		Assertions.assertNotNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Tests that a released run gives the key back.
	 */
	@Test
	public void testReleaseGivesTheKeyBack() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);

		final LocalThrottleReservation run = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		throttle.release(run);
		Assertions.assertNotNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
		Assertions.assertNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Tests that releasing a run does not undo a later run of the key.
	 */
	@Test
	public void testReleaseKeepsALaterRun() {
		final AtomicReference<LocalDateTime> clock = new AtomicReference<>(LocalDateTime.of(2026, 1, 1, 10, 0));
		final LocalThrottle throttle = this.createThrottle(clock);

		final LocalThrottleReservation oldRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		clock.set(clock.get().plus(LocalThrottleTest.WAIT_PERIOD).plusSeconds(1));
		final LocalThrottleReservation laterRun = throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null);
		Assertions.assertNotNull(laterRun);

		throttle.release(oldRun);
		Assertions.assertEquals(laterRun.acquiredAt(), throttle.getLastRun("key").getLastRunAt());
		Assertions.assertNull(throttle.tryAcquire("key", LocalThrottleTest.WAIT_PERIOD, null));
	}

	/**
	 * Tests that the expired entries are cleared on the given clock.
	 */
	@Test
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

}
