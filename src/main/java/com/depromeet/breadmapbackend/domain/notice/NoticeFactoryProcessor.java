package com.depromeet.breadmapbackend.domain.notice;

import java.util.List;

import com.depromeet.breadmapbackend.domain.notice.dto.BasicNoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;

public interface NoticeFactoryProcessor {
	String getImage(Notice notice);

	List<Notice> createNotice(NoticeEventDto noticeEventDto);

	List<Notice> createNotice(BasicNoticeEventDto basicNoticeEventDto);
}
