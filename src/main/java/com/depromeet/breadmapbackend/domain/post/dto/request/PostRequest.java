package com.depromeet.breadmapbackend.domain.post.dto.request;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.depromeet.breadmapbackend.domain.post.PostTopic;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

public record PostRequest(
	@NotNull @Size(min = 10, max = 40) String title,
	@NotNull @Size(min = 10, max = 400) String content,
	@Size(max = 10) List<String> images,
	@EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
	PostTopic postTopic

) {
}
