package com.depromeet.breadmapbackend.domain.notice;

import java.util.List;

import com.depromeet.breadmapbackend.domain.notice.type.NoticeEventDto;

public interface NoticeFactoryProcessor {
	String getImage(Notice notice);

	List<Notice> createNotice(NoticeEventDto noticeEventDto);
}
