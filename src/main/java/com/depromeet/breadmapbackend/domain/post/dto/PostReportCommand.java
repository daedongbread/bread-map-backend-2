package com.depromeet.breadmapbackend.domain.post.dto;

import com.depromeet.breadmapbackend.domain.post.report.PostReportReason;

/**
 * PostReportCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/22
 */
public record PostReportCommand(
	PostReportReason reason,
	String content,
	Long postId
) {
}
