package com.depromeet.breadmapbackend.domain.notice.factory.push;

import java.util.List;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;

public interface NoticeFactory {

	boolean support(NoticeType noticeType);

	String getImage(Notice notice);

	List<Notice> createNotice(NoticeEventDto noticeEventDto);
}
