package com.depromeet.breadmapbackend.domain.notice.dto;

import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;

import lombok.Builder;

/**
 * BaseNoticeEvent
 *
 * @author jaypark
 * @version 1.0.0
 * @since 10/11/23
 */

public record NoticeEventDto(
	Long contentId,
	NoticeType noticeType,
	Long userId
) {

	@Builder
	public NoticeEventDto {
	}
}
