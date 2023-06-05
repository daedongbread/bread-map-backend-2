package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

public interface NoticeService {
	//    void addNoticeToken(String oAuthId, NoticeTokenRequest request);
	//    void deleteNoticeToken(NoticeTokenDeleteEvent event);
	void addNotice(final NoticeEvent noticeEvent);

	PageResponseDto<NoticeDto> getNoticeList(String oAuthId, NoticeDayType type, Long lastId, int page);
}
