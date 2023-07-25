package com.depromeet.breadmapbackend.domain.post.dto.request;

import java.util.List;

import com.depromeet.breadmapbackend.domain.post.PostTopic;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

public record PostRequest(
	String title,
	String content,
	List<String> images,
	@EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
	PostTopic postTopic

) {
}
