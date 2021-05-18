package org.coldis.library.helper;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Date/time helper.
 */
public class DateTimeHelper {

	/**
	 * Date time formatter.
	 */
	public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().parseStrict().optionalStart().append(DateTimeFormatter.ISO_DATE)
			.optionalEnd().optionalStart().appendLiteral('T').append(DateTimeFormatter.ISO_TIME).optionalEnd().parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
			.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0).parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
			.optionalStart().appendOffset("+HHMMss", "X").optionalEnd().toFormatter();

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
		return DateTimeHelper.CLOCK;
	}

	/**
	 * Sets the clock to be used by the helper.
	 *
	 * @param clock Clock to be used by the helper.
	 */
	public static void setClock(
			final Clock clock) {
		DateTimeHelper.CLOCK = clock;
	}

	/**
	 * Adjust the clock for the given offsets.
	 *
	 * @param  offsets The offsets to adjust the clock.
	 * @return         The original clock (before adjustments).
	 */
	public static Clock adjustClock(
			final Duration... offsets) {
		// Gets the original clock.
		final Clock originalClock = DateTimeHelper.getClock();
		Clock currentClock = originalClock;
		// For each offset.
		if (offsets != null) {
			for (final Duration currentOffset : offsets) {
				// Adds the offset to the clock.
				currentClock = Clock.offset(currentClock, currentOffset);
			}
		}
		// Re-sets the clock.
		DateTimeHelper.CLOCK = currentClock;
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

	/**
	 * Converts the date to timestamp.
	 *
	 * @param  dateTime Date time.
	 * @return          Date as timestamp.
	 */
	public static Long toTimestamp(
			final LocalDateTime dateTime) {
		return (dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
	}

}
