package org.coldis.library.helper;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Date/time helper.
 */
public class DateTimeHelper {

	/**
	 * Clock to be used by the helper.
	 */
	private static Clock CLOCK = Clock.systemDefaultZone();

	/**
	 * Gets the clock to be used by the helper..
	 * 
	 * @return the clock to be used by the helper..
	 */
	public static Clock getClock() {
		return CLOCK;
	}

	/**
	 * Sets the clock to be used by the helper.
	 *
	 * @param clock Clock to be used by the helper.
	 */
	public static void setClock(final Clock clock) {
		DateTimeHelper.CLOCK = clock;
	}

	/**
	 * Adjust the clock for the given offsets.
	 * 
	 * @param offsets The offsets to adjust the clock.
	 * @return The original clock (before adjustments).
	 */
	public static Clock adjustClock(Duration... offsets) {
		// Gets the original clock.
		Clock originalClock = getClock();
		Clock currentClock = originalClock;
		// For each offset.
		if (offsets != null) {
			for (Duration currentOffset : offsets) {
				// Adds the offset to the clock.
				currentClock = Clock.offset(currentClock, currentOffset);
			}
		}
		// Re-sets the clock.
		CLOCK = currentClock;
		// Returns the original clock.
		return originalClock;
	}

	/**
	 * Gets the current local date/time using the helper clock.
	 *
	 * @return The current local date/time using the helper clock.
	 */
	public static LocalDateTime getCurrentLocalDateTime() {
		return LocalDateTime.now(DateTimeHelper.CLOCK);
	}

	/**
	 * Gets the current local date using the helper clock.
	 *
	 * @return The current local date using the helper clock.
	 */
	public static LocalDate getCurrentLocalDate() {
		return LocalDate.now(DateTimeHelper.CLOCK);
	}

	/**
	 * Gets the current local time using the helper clock.
	 *
	 * @return The current local time using the helper clock.
	 */
	public static LocalTime getCurrentLocalTime() {
		return LocalTime.now(DateTimeHelper.CLOCK);
	}

}
