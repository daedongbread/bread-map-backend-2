package com.depromeet.breadmapbackend.domain.notice.content;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;

@Component
public class FollowNoticeContent implements NoticeContent {

	private static final String NOTICE_TITLE_FORMAT = "%s님이 회원님을 팔로우하기 시작했어요";
	private static final NoticeType SUPPORT_TYPE = NoticeType.FOLLOW;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		return notice.getFromUser().getUserInfo().getImage();
	}

	@Override
	public String getTitle(final Notice notice) {
		return NOTICE_TITLE_FORMAT.formatted(getNickName(notice));
	}
}
