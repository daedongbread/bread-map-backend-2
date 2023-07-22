package com.depromeet.breadmapbackend.domain.admin.feed.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedStatus {
	POSTING("POSTING"),
	INACTIVATED("INACTIVATED");

	private final String code;

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static FeedStatus findByCode(String code) {
		return Stream.of(FeedStatus.values())
			.filter(c -> c.code.equalsIgnoreCase(code)) // 소문자로 받음
			.findFirst()
			.orElse(null);
	}
}
