package com.depromeet.breadmapbackend.domain.notice.factory.push;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.post.comment.Comment;
import com.depromeet.breadmapbackend.domain.post.comment.CommentRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentLikeNoticeFactory implements NoticeFactory {

	private static final String NOTICE_CONTENT_FORMAT = "내 댓글을 %s님이 좋아해요!";
	private static final String NOTICE_TITLE_FORMAT = "댓글 좋아요 알림";
	private static final List<NoticeType> SUPPORT_TYPE = List.of(
		NoticeType.COMMENT_LIKE,
		NoticeType.REVIEW_COMMENT_LIKE
	);
	private final CustomAWSS3Properties customAwss3Properties;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE.contains(noticeType);
	}

	@Override
	public String getImage(final Notice notice) {
		return customAwss3Properties.getCloudFront() + "/" +
			customAwss3Properties.getDefaultImage().getLike()
			+ ".png";
	}

	@Override
	public List<Notice> createNotice(final NoticeEventDto noticeEventDto) {
		final Comment comment = commentRepository.findById(noticeEventDto.contentId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND));
		final User fromUser = userRepository.findById(noticeEventDto.userId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		return List.of(Notice.createNoticeWithContentAndSubContentId(
			comment.getUser(),
			NOTICE_TITLE_FORMAT,
			noticeEventDto.contentId(),
			NOTICE_CONTENT_FORMAT,
			fromUser.getNickName(),
			noticeEventDto.noticeType(),
			noticeEventDto.subContentId()
		));
	}
}
