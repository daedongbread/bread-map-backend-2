package com.depromeet.breadmapbackend.domain.post.dto.request;

import java.util.List;

import com.depromeet.breadmapbackend.domain.post.PostTopic;

public record PostRequest(
	String title,
	String content,
	List<String> images,
	PostTopic postTopic
) {
}
