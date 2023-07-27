package com.depromeet.breadmapbackend.domain.report.dto.request;

import com.depromeet.breadmapbackend.domain.report.ReportReason;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

/**
 * ReportRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
public record ReportRequest(
	@EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
	ReportReason reason,
	String content
) {
}
