package com.depromeet.breadmapbackend.domain.notice.type;

import com.depromeet.breadmapbackend.domain.notice.NoticeType;

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
	String content,
	NoticeType noticeType,
	Long userId
) {

	@Builder
	public NoticeEventDto {
	}
}
