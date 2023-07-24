package com.depromeet.breadmapbackend.domain.admin.feed.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CommonFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.LandingFeedRequestDto;

public class LandingFeedTest {

	@DisplayName("랜딩 피드를 수정한다")
	@Test
	void 랜딩피드_수정_테스트() {

		//given
		LandingFeed landingFeed = LandingFeed.builder()
			.feedType(FeedType.LANDING.toString())
			.subTitle("8월 추천 빵집")
			.activeTime(LocalDateTime.now())
			.introduction("안녕하세요")
			.conclusion("다음에 또 만나요")
			.activated(FeedStatus.POSTING)
			.thumbnailUrl("testUrl")
			.category(new Category("테스트 카테고리"))
			.redirectUrl("testRedirectUrl")
			.build();

		Category category = new Category("업데이트 카테고리");

		FeedRequestDto updateRequest = new FeedRequestDto(
			CommonFeedRequestDto.builder()
				.feedType(FeedType.LANDING)
				.subTitle("업데이트된 8월 추천 빵집")
				.activeTime("2023-07-025T00:00:00")
				.introduction("업데이트된 안녕하세요")
				.conclusion("업데이트된 다음에 또 만나요")
				.activated(FeedStatus.POSTING)
				.thumbnailUrl("업데이트된 testUrl")
				.build(),
			new LandingFeedRequestDto("updateRedirectUrl")
		);

		//when
		landingFeed.update(category, updateRequest);

		//then
		assertThat(landingFeed)
			.extracting("subTitle", "introduction", "conclusion", "redirectUrl")
			.containsOnly(
				"업데이트된 8월 추천 빵집", "업데이트된 안녕하세요", "업데이트된 다음에 또 만나요", "updateRedirectUrl"
			);
	}
}
