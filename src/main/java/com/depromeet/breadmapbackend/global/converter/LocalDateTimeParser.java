package com.depromeet.breadmapbackend.global.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTimeParser
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/17
 */
public class LocalDateTimeParser {

	public static LocalDate parse(final String stringLocalDate) {
		return LocalDate.parse(
			stringLocalDate,
			DateTimeFormatter.ISO_LOCAL_DATE
		);
	}

	public static String parse(final LocalDate localDate) {
		return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
	}

}
