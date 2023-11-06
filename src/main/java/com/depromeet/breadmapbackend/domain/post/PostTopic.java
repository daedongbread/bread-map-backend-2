package com.depromeet.breadmapbackend.domain.post;

import java.util.Arrays;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostTopic {
	ALL,
	REVIEW,
	EVENT,
	EATEN_BREAD,
	BREAD_STORY,
	FREE_TALK,
	;

	public static PostTopic of(final String topic) {
		return Arrays.stream(PostTopic.values())
			.filter(postTopic -> postTopic.name().equals(topic))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.INVALID_POST_TOPIC));
	}
}
