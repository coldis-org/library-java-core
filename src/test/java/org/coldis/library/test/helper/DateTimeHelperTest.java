package org.coldis.library.test.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.coldis.library.helper.DateTimeHelper;
import org.junit.jupiter.api.Test;

/**
 * Date time helper test.
 */
public class DateTimeHelperTest {

	/**
	 * Tests the formatter.
	 */
	@Test
	public void testFormatter() {
		LocalDate.parse("2019-10-12", DateTimeHelper.DATE_TIME_FORMATTER);
		LocalDate.parse("2019-10-12T10:10", DateTimeHelper.DATE_TIME_FORMATTER);
		LocalDateTime.parse("2019-10-12T10:10", DateTimeHelper.DATE_TIME_FORMATTER);
		LocalDate.parse("2019-10-12T10:10:10", DateTimeHelper.DATE_TIME_FORMATTER);
		LocalDateTime.parse("2019-10-12T10:10:10", DateTimeHelper.DATE_TIME_FORMATTER);
	}

}
