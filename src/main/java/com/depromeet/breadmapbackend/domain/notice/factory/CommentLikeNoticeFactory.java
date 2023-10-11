package com.depromeet.breadmapbackend.domain.notice.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.domain.notice.type.NoticeEventDto;
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

	private static final String NOTICE_TITLE_FORMAT = "내 댓글을 %s님이 좋아해요!";
	private static final NoticeType SUPPORT_TYPE = NoticeType.COMMENT_LIKE;
	private final CustomAWSS3Properties customAwss3Properties;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;

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
		final Comment comment = commentRepository.findById(noticeEventDto.contentId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND));
		final User fromUser = userRepository.findById(noticeEventDto.userId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		return List.of(Notice.createNoticeWithContent(
			comment.getUser(),
			NOTICE_TITLE_FORMAT.formatted(fromUser.getNickName()),
			noticeEventDto.contentId(),
			comment.getContent(),
			noticeEventDto.noticeType()
		));
	}

}
