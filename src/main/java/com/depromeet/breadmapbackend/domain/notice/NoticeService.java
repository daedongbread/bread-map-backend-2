package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.notice.type.NoticeEventDto;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

public interface NoticeService {
	void addNotice(final NoticeEventDto noticeEventDto);

	PageResponseDto<NoticeDto> getNoticeList(String oAuthId, NoticeDayType type, Long lastId, int page);
}
