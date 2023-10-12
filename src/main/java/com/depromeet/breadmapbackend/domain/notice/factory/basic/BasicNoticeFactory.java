package com.depromeet.breadmapbackend.domain.notice.factory.basic;

import java.util.List;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.dto.BasicNoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;

public interface BasicNoticeFactory {

	boolean support(NoticeType noticeType);

	String getImage(Notice notice);

	List<Notice> createNotice(BasicNoticeEventDto basicNoticeEventDto);
}
