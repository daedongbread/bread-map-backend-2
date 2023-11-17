package com.depromeet.breadmapbackend.domain.notice.factory.push;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.post.PostRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommunityLikeNoticeFactory implements NoticeFactory {
	private static final String COMMUNITY_CONTENT_FORMAT = "내 게시글을 %s님이 좋아해요!";
	private static final String COMMUNITY_TITLE_FORMAT = "좋아요 알림";
	private static final NoticeType SUPPORT_TYPE = NoticeType.COMMUNITY_LIKE;
	private final CustomAWSS3Properties customAwss3Properties;
	private final UserRepository userRepository;
	private final PostRepository postRepository;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		return customAwss3Properties.getCloudFront() + "/" +
			customAwss3Properties.getDefaultImage().getLike()
			+ ".png";
	}

	@Override
	public List<Notice> createNotice(final NoticeEventDto noticeEventDto) {
		final Post post = postRepository.findById(noticeEventDto.contentId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));
		final User fromUser = userRepository.findById(noticeEventDto.userId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		return List.of(Notice.createNoticeWithContent(
			post.getUser(),
			COMMUNITY_TITLE_FORMAT,
			noticeEventDto.contentId(),
			COMMUNITY_CONTENT_FORMAT,
			fromUser.getNickName(),
			noticeEventDto.noticeType()
		));
	}
}
