package com.depromeet.breadmapbackend.domain.notice.factory.push;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.repository.PostAdminRepository;
import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

/**
 * NewEventNoticeFatoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 10/11/23
 */

@Component
@RequiredArgsConstructor
public class NewEventNoticeFactoryImpl implements NoticeFactory {

	private static final String NOTICE_TITLE_FORMAT = "( 이벤트 ) %s";
	private static final NoticeType SUPPORT_TYPE = NoticeType.EVENT;
	private final CustomAWSS3Properties customAwss3Properties;
	private final UserRepository userRepository;
	private final PostAdminRepository postAdminRepository;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		return customAwss3Properties.getCloudFront() + "/" +
			customAwss3Properties.getDefaultImage().getFlag()
			+ ".png";  // TODO 이벤트 아이콘 이미지 변경
	}

	@Override
	public List<Notice> createNotice(final NoticeEventDto noticeEventDto) {
		final List<User> users = userRepository.findUserWithNoticeTokens();
		final PostManagerMapper postManagerMapper =
			postAdminRepository.findPostManagerMapperById(noticeEventDto.contentId())
				.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));

		return users.stream().map(
				user -> Notice.createNoticeWithOutContent(
					user,
					NOTICE_TITLE_FORMAT.formatted(postManagerMapper.getPost().getTitle()),
					SUPPORT_TYPE
				)
			)
			.toList();
	}
}
