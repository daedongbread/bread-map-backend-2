package com.depromeet.breadmapbackend.domain.notice;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

class NoticeServiceImplTest extends NoticeServiceTest {

	@Autowired
	private NoticeServiceImpl sut;
	@Autowired
	private CustomAWSS3Properties customAWSS3Properties;

	@Test
	@Sql("classpath:notice-test-data.sql")
	void 알림_타입에_따른_메시지와_이미지_생성_테스트() throws Exception {
		final String oAuthId = "APPLE_111";
		final int page = 0;

		final PageResponseDto<NoticeDto> result = sut.getNoticeList(oAuthId, page);

		assertResults(result);
	}

	private void assertResults(final PageResponseDto<NoticeDto> result) {
		assertThat(result.getContents().stream().map(NoticeDto::getTitle))
			.containsExactly(
				"title test9",
				"title test8",
				"title test7",
				"title test6",
				"title test5",
				"title test4",
				"title test3",
				"title test2",
				"title test1"
			);

		assertThat(result.getContents().stream().map(NoticeDto::getImage))
			.containsExactly(
				"%s/%s.png".formatted(customAWSS3Properties.getCloudFront(),
					customAWSS3Properties.getDefaultImage().getUser()),
				"%s/%s.png".formatted(customAWSS3Properties.getCloudFront(),
					customAWSS3Properties.getDefaultImage().getUser()),
				"%s/%s.png".formatted(customAWSS3Properties.getCloudFront(),
					customAWSS3Properties.getDefaultImage().getLike()),
				"%s/%s.png".formatted(customAWSS3Properties.getCloudFront(),
					customAWSS3Properties.getDefaultImage().getUser()),
				"%s/%s.png".formatted(customAWSS3Properties.getCloudFront(),
					customAWSS3Properties.getDefaultImage().getLike()),
				"%s/%s.png".formatted(customAWSS3Properties.getCloudFront(),
					customAWSS3Properties.getDefaultImage().getComment()),
				"%s/%s.png".formatted(customAWSS3Properties.getCloudFront(),
					customAWSS3Properties.getDefaultImage().getLike()),
				"%s/%s.png".formatted(customAWSS3Properties.getCloudFront(),
					customAWSS3Properties.getDefaultImage().getComment()),
				"%s/%s.png".formatted(customAWSS3Properties.getCloudFront(),
					customAWSS3Properties.getDefaultImage().getUser())
			);
	}
}