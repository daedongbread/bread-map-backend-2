package com.depromeet.breadmapbackend.domain.review.post.dto.request;

import java.util.List;

public record PostRequest(
	String title,
	String content,
	List<String> images
) {

}
