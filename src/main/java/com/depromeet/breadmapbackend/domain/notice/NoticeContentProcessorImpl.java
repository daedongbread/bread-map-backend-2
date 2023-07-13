package com.depromeet.breadmapbackend.domain.notice;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.content.NoticeContent;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NoticeContentProcessorImpl implements NoticeContentProcessor {

	private final List<NoticeContent> noticeContentList;

	@Override
	public String getImage(final Notice notice) {
		NoticeContent noticeContent = routingNoticeContentCaller(notice);
		return noticeContent.getImage(notice);
	}

	@Override
	public String getTitle(final Notice notice) {
		NoticeContent noticeContent = routingNoticeContentCaller(notice);
		return noticeContent.getTitle(notice);
	}

	private NoticeContent routingNoticeContentCaller(final Notice notice) {
		return noticeContentList.stream()
			.filter(noticeContent -> noticeContent.support(notice.getType()))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.NOTICE_TYPE_EXCEPTION));
	}
}
