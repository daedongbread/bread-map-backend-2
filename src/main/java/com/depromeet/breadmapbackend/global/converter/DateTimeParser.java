package com.depromeet.breadmapbackend.global.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeParser {

	private DateTimeParser() {
	}

	public static LocalDateTime parse(final String stringLocalDate) {
		return LocalDateTime.parse(
			stringLocalDate,
			DateTimeFormatter.ISO_LOCAL_DATE_TIME
		);
	}

	public static String parse(final LocalDateTime localDate) {
		return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}
