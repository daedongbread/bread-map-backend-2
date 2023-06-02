package com.depromeet.breadmapbackend.domain.notice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomFirebaseProperties;
import com.depromeet.breadmapbackend.utils.TestConfig;

@DataJpaTest
@Import({
	NoticeServiceImpl.class,
	NoticeQueryRepository.class,
	FcmService.class,
	DefaultNoticeGenerateFactory.class,
	TestConfig.class,
})
@EnableConfigurationProperties({
	CustomFirebaseProperties.class,
	CustomAWSS3Properties.class
})
class NoticeServiceImplTest {

	@Autowired
	private NoticeServiceImpl sut;

	@Test
	void generateMessageTest() throws Exception {

		sut.getNoticeList("awertwaet", NoticeDayType.BEFORE, 1L, 1);

	}

}