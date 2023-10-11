package com.depromeet.breadmapbackend.domain.notice;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.factory.NoticeFactory;
import com.depromeet.breadmapbackend.domain.notice.type.NoticeEventDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NoticeFactoryProcessorImpl implements NoticeFactoryProcessor {

	private final List<NoticeFactory> noticeFactoryList;

	@Override
	public String getImage(final Notice notice) {
		NoticeFactory noticeFactory = routingNoticeContentCaller(notice.getType());
		return noticeFactory.getImage(notice);
	}

	@Override
	public List<Notice> createNotice(final NoticeEventDto noticeEventDto) {
		NoticeFactory noticeFactory = routingNoticeContentCaller(noticeEventDto.noticeType());
		return noticeFactory.createNotice(noticeEventDto);
	}

	private NoticeFactory routingNoticeContentCaller(final NoticeType noticeType) {
		return noticeFactoryList.stream()
			.filter(noticeContent -> noticeContent.support(noticeType))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.NOTICE_TYPE_EXCEPTION));
	}
}
