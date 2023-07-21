package com.depromeet.breadmapbackend.domain.post.dto.request;

import com.depromeet.breadmapbackend.domain.post.report.PostReportReason;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

public record PostReportRequest(
	@EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
	PostReportReason reason,
	String content
) {
}
