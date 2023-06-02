package com.depromeet.breadmapbackend.domain.notice;

import static com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto.*;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

@Component
public class DefaultNoticeGenerateFactory implements NoticeGenerateFactory {

	private final CustomAWSS3Properties customAwss3Properties;

	public DefaultNoticeGenerateFactory(final CustomAWSS3Properties customAwss3Properties) {
		this.customAwss3Properties = customAwss3Properties;
	}

	@Override
	public NoticeDto generateNotice(
		final Notice notice,
		final boolean isFollow
	) {
		try {
			final NoticeBaseInfo noticeBaseInfo = notice.getType()
				.getNoticeBaseInfo(notice, customAwss3Properties.getDefaultImage());
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
	public String getTitle(final Notice notice) {
		try {
			return notice.getType()
				.getNoticeBaseInfo(notice, customAwss3Properties.getDefaultImage())
				.getTitle();
		} catch (IllegalArgumentException e) {
			throw new DaedongException(DaedongStatus.NOTICE_TYPE_EXCEPTION);
		}
	}
}
