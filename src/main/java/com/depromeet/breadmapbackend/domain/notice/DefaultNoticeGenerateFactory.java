package com.depromeet.breadmapbackend.domain.notice;

import static com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto.*;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeFcmDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultNoticeGenerateFactory implements NoticeGenerateFactory {

	private final CustomAWSS3Properties customAwss3Properties;

	@Override
	public NoticeDto generateNoticeDtoForApi(final Notice notice, final boolean isFollow) {
		try {
			final NoticeBaseInfo noticeBaseInfo = getNoticeBaseInfo(notice);
			return builder()
				.image(notice.getType() == NoticeType.FOLLOW ?
					noticeBaseInfo.getImage() :
					customAwss3Properties.getCloudFront() + "/" + noticeBaseInfo.getImage() + ".png"
				)
				.title(noticeBaseInfo.getTitle())
				.isFollow(isFollow)
				.notice(notice)
				.build();
		} catch (IllegalArgumentException e) {
			throw new DaedongException(DaedongStatus.NOTICE_TYPE_EXCEPTION);
		}
	}

	@Override
	public NoticeFcmDto generateNoticeDtoForFcm(final Notice notice) {
		try {
			return NoticeFcmDto.builder()
				.userId(notice.getUserId())
				.title(getNoticeBaseInfo(notice).getTitle())
				.content(notice.getContent())
				.contentId(notice.getContentId())
				.type(notice.getType())
				.build();
		} catch (IllegalArgumentException e) {
			throw new DaedongException(DaedongStatus.NOTICE_TYPE_EXCEPTION);
		}
	}

	private NoticeBaseInfo getNoticeBaseInfo(final Notice notice) {
		return notice.getType()
			.getNoticeBaseInfo(notice, customAwss3Properties.getDefaultImage());
	}
}
