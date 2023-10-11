package com.depromeet.breadmapbackend.domain.notice.factory;

import java.util.List;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.domain.notice.type.NoticeEventDto;

public interface NoticeFactory {

	boolean support(NoticeType noticeType);

	String getImage(Notice notice);

	List<Notice> createNotice(NoticeEventDto noticeEventDto);
}
