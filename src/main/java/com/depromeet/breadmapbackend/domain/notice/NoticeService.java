package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.notice.dto.BasicNoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

public interface NoticeService {
	void sendPushNotice(final NoticeEventDto noticeEventDto);

	void saveNotice(final BasicNoticeEventDto basicNoticeEventDto);

	PageResponseDto<NoticeDto> getNoticeList(String oAuthId, NoticeDayType type, Long lastId, int page);
}
