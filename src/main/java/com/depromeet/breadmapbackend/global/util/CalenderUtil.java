package com.depromeet.breadmapbackend.global.util;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * CalenderUtil
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
public class CalenderUtil {
	public static String getYearWeekOfMonth(final LocalDate date) {
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		int weekOfMonth = date.get(weekFields.weekOfMonth());
		return date.getYear() + "-" + date.getMonthValue() + "-" + weekOfMonth;
	}
}
