package com.depromeet.breadmapbackend.domain.notice.factory.push;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.follow.Follow;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FollowNoticeFactory implements NoticeFactory {

	private static final String NOTICE_TITLE_FORMAT = "%s님이 회원님을 팔로우하기 시작했어요";
	private static final NoticeType SUPPORT_TYPE = NoticeType.FOLLOW;
	private final UserRepository userRepository;
	private final FollowRepository followRepository;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		assert notice.getContentId() != null;
		return userRepository.findById(notice.getContentId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND))
			.getUserInfo()
			.getImage();
	}

	@Override
	public List<Notice> createNotice(final NoticeEventDto noticeEventDto) {
		final User toUser = userRepository.findUserWithNoticeTokensByUserId(noticeEventDto.userId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		final User fromUser = userRepository.findById(noticeEventDto.contentId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		final Optional<Follow> isFollow = followRepository.findByFromUserAndToUser(toUser, fromUser);
		return List.of(Notice.createNoticeWithContent(
			toUser,
			NOTICE_TITLE_FORMAT.formatted(fromUser.getNickName()),
			fromUser.getId(),
			isFollow.isPresent() ? "FOLLOW" : "UNFOLLOW",
			SUPPORT_TYPE
		));
	}
}
