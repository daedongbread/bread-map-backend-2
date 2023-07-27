package com.depromeet.breadmapbackend.domain.admin.feed.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CommonFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CurationFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.global.exception.DaedongException;

public class CurationFeedTest {

	@Test
	@DisplayName("큐레이션 피드에 추천하는 빵집을 넣어 생성한다")
	void 큐레이션_추천빵집_테스트() {

		//given
		CurationFeed curationFeed = CurationFeed.builder()
			.category(new Category("테스트 카테고리"))
			.likes(new FeedLikes(new ArrayList<>()))
			.build();

		FeedRequestDto request = new FeedRequestDto(
			CommonFeedRequestDto.builder()
				.feedType(FeedType.CURATION)
				.subTitle("8월 추천 빵집")
				.activeTime("2023-07-25T00:00:00")
				.introduction("안녕하세요")
				.conclusion("다음에 또 만나요")
				.activated(FeedStatus.POSTING)
				.thumbnailUrl("testUrl")
				.build(),
			List.of(
				CurationFeedRequestDto.builder()
					.bakeryId(1L)
					.productId(1L)
					.reason("맛있어요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(2L)
					.productId(2L)
					.reason("예뻐요")
					.build()
			)
		);

		List<Bakery> bakeries = List.of(
			Bakery.builder()
				.id(1L)
				.name("추천빵집 1")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(2L)
				.name("추천빵집 2")
				.status(BakeryStatus.POSTING)
				.build()
		);

		//when
		curationFeed.addAll(bakeries, request);

		//then
		assertThat(curationFeed.getBakeries().getBakeries().size()).isEqualTo(2);
	}

	@Test
	@DisplayName("큐레이션 피드에 추천하는 빵집을 추천할 수 있다 - UNPOSTING STATUS인 빵집일 경우 실패")
	void 큐레이션_추천빵집_테스트_UNPOSTING_STATUS_실패() {

		//given
		CurationFeed curationFeed = CurationFeed.builder()
			.category(new Category("테스트 카테고리"))
			.likes(new FeedLikes(new ArrayList<>()))
			.build();

		FeedRequestDto request = new FeedRequestDto(
			CommonFeedRequestDto.builder()
				.feedType(FeedType.CURATION)
				.subTitle("8월 추천 빵집")
				.activeTime("2023-07-25T00:00:00")
				.introduction("안녕하세요")
				.conclusion("다음에 또 만나요")
				.activated(FeedStatus.POSTING)
				.thumbnailUrl("testUrl")
				.build(),
			List.of(
				CurationFeedRequestDto.builder()
					.bakeryId(1L)
					.productId(1L)
					.reason("맛있어요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(2L)
					.productId(2L)
					.reason("예뻐요")
					.build()
			)
		);

		List<Bakery> bakeries = List.of(
			Bakery.builder()
				.id(1L)
				.name("추천빵집 1")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(2L)
				.name("추천빵집 2")
				.status(BakeryStatus.UNPOSTING)
				.build()
		);

		//then
		assertThatCode(() -> curationFeed.addAll(bakeries, request))
			.isInstanceOf(DaedongException.class)
			.extracting("daedongStatus.code")
			.isEqualTo(40096);
	}

	@Test
	@DisplayName("큐레이션 피드에 추천하는 빵집을 추천할 수 있다 - 한 피드당 빵집 5개 이상 추천 시 실패")
	void 큐레이션_추천빵집_테스트_5개이상_실패() {

		//given
		CurationFeed curationFeed = CurationFeed.builder()
			.category(new Category("테스트 카테고리"))
			.likes(new FeedLikes(new ArrayList<>()))
			.build();

		FeedRequestDto request = new FeedRequestDto(
			CommonFeedRequestDto.builder()
				.feedType(FeedType.CURATION)
				.subTitle("8월 추천 빵집")
				.activeTime("2023-07-25T00:00:00")
				.introduction("안녕하세요")
				.conclusion("다음에 또 만나요")
				.activated(FeedStatus.POSTING)
				.thumbnailUrl("testUrl")
				.build(),
			List.of(
				CurationFeedRequestDto.builder()
					.bakeryId(1L)
					.productId(1L)
					.reason("맛있어요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(2L)
					.productId(2L)
					.reason("예뻐요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(3L)
					.productId(3L)
					.reason("조용해요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(4L)
					.productId(4L)
					.reason("사진찍기좋아요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(5L)
					.productId(5L)
					.reason("친절해요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(6L)
					.productId(6L)
					.reason("예외에요")
					.build()
			)
		);

		List<Bakery> bakeries = List.of(
			Bakery.builder()
				.id(1L)
				.name("추천빵집 1")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(2L)
				.name("추천빵집 2")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(3L)
				.name("추천빵집 3")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(4L)
				.name("추천빵집 4")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(5L)
				.name("추천빵집 5")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(6L)
				.name("추천빵집 6")
				.status(BakeryStatus.POSTING)
				.build()
		);

		//then
		assertThatCode(() -> curationFeed.addAll(bakeries, request))
			.isInstanceOf(DaedongException.class)
			.extracting("daedongStatus.code")
			.isEqualTo(40094);
	}

	@Test
	@DisplayName("큐레이션 피드에 추천하는 빵집을 추천할 수 있다 - 같은 피드에 중복된 빵집이 있으면 실패")
	void 큐레이션_추천빵집_테스트_중복빵집등록_실패() {

		//given
		CurationFeed curationFeed = CurationFeed.builder()
			.category(new Category("테스트 카테고리"))
			.likes(new FeedLikes(new ArrayList<>()))
			.build();

		FeedRequestDto request = new FeedRequestDto(
			CommonFeedRequestDto.builder()
				.feedType(FeedType.CURATION)
				.subTitle("8월 추천 빵집")
				.activeTime("2023-07-25T00:00:00")
				.introduction("안녕하세요")
				.conclusion("다음에 또 만나요")
				.activated(FeedStatus.POSTING)
				.thumbnailUrl("testUrl")
				.build(),
			List.of(
				CurationFeedRequestDto.builder()
					.bakeryId(1L)
					.productId(1L)
					.reason("맛있어요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(2L)
					.productId(2L)
					.reason("예뻐요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(2L)
					.productId(2L)
					.reason("중복이에요")
					.build()
			)
		);

		List<Bakery> bakeries = List.of(
			Bakery.builder()
				.id(1L)
				.name("추천빵집 1")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(2L)
				.name("추천빵집 2")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(2L)
				.name("추천빵집 2")
				.status(BakeryStatus.POSTING)
				.build()
		);

		//then
		assertThatCode(() -> curationFeed.addAll(bakeries, request))
			.isInstanceOf(DaedongException.class)
			.extracting("daedongStatus.code")
			.isEqualTo(40991);
	}

	@Test
	@DisplayName("큐레이션에 피드에 포함되어 있는 빵집을 모두 삭제한다")
	void 큐레이션_빵집_삭제_테스트() {

		//given
		CurationFeed curationFeed = CurationFeed.builder()
			.category(new Category("테스트 카테고리"))
			.likes(new FeedLikes(new ArrayList<>()))
			.build();

		FeedRequestDto request = new FeedRequestDto(
			CommonFeedRequestDto.builder()
				.feedType(FeedType.CURATION)
				.subTitle("8월 추천 빵집")
				.activeTime("2023-07-25T00:00:00")
				.introduction("안녕하세요")
				.conclusion("다음에 또 만나요")
				.activated(FeedStatus.POSTING)
				.thumbnailUrl("testUrl")
				.build(),
			List.of(
				CurationFeedRequestDto.builder()
					.bakeryId(1L)
					.productId(1L)
					.reason("맛있어요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(2L)
					.productId(2L)
					.reason("예뻐요")
					.build()
			)
		);

		List<Bakery> bakeries = List.of(
			Bakery.builder()
				.id(1L)
				.name("추천빵집 1")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(2L)
				.name("추천빵집 2")
				.status(BakeryStatus.POSTING)
				.build()
		);

		curationFeed.addAll(bakeries, request);

		//when
		curationFeed.removeAllBakeries();

		//then
		assertThat(curationFeed.getBakeries().getBakeries().size()).isEqualTo(0);
	}

	@Test
	@DisplayName("큐레이션 피드를 수정한다")
	void 큐레이션_피드를_수정한다() {

		//given
		CurationFeed curationFeed = CurationFeed.builder()
			.category(new Category("테스트 카테고리"))
			.likes(new FeedLikes(new ArrayList<>()))
			.build();

		FeedRequestDto request = new FeedRequestDto(
			CommonFeedRequestDto.builder()
				.feedType(FeedType.CURATION)
				.subTitle("8월 추천 빵집")
				.activeTime("2023-07-25T00:00:00")
				.introduction("안녕하세요")
				.conclusion("다음에 또 만나요")
				.activated(FeedStatus.POSTING)
				.thumbnailUrl("testUrl")
				.build(),
			List.of(
				CurationFeedRequestDto.builder()
					.bakeryId(1L)
					.productId(1L)
					.reason("맛있어요")
					.build(),
				CurationFeedRequestDto.builder()
					.bakeryId(2L)
					.productId(2L)
					.reason("예뻐요")
					.build()
			)
		);

		List<Bakery> bakeries = List.of(
			Bakery.builder()
				.id(1L)
				.name("추천빵집 1")
				.status(BakeryStatus.POSTING)
				.build(),
			Bakery.builder()
				.id(2L)
				.name("추천빵집 2")
				.status(BakeryStatus.POSTING)
				.build()
		);

		FeedRequestDto updateRequest = new FeedRequestDto(
			CommonFeedRequestDto.builder()
				.feedType(FeedType.CURATION)
				.subTitle("업데이트된 8월 추천 빵집")
				.activeTime("2023-07-25T00:00:00")
				.introduction("업데이트된 안녕하세요")
				.conclusion("업데이트된 다음에 또 만나요")
				.activated(FeedStatus.POSTING)
				.thumbnailUrl("업데이트된 testUrl")
				.build(),
			List.of(
				CurationFeedRequestDto.builder()
					.bakeryId(3L)
					.productId(3L)
					.reason("업데이트된 맛있어요")
					.build()
			)
		);

		List<Bakery> updateBakeries = List.of(
			Bakery.builder()
				.id(3L)
				.name("추천빵집 3")
				.status(BakeryStatus.POSTING)
				.build()
		);

		Category updateCategory = new Category("업데이트된 카테고리");

		curationFeed.addAll(bakeries, request);

		//when
		curationFeed.update(updateBakeries, updateCategory, updateRequest);

		//then
		assertThat(curationFeed)
			.extracting("subTitle", "introduction", "conclusion")
			.containsOnly(
				"업데이트된 8월 추천 빵집", "업데이트된 안녕하세요", "업데이트된 다음에 또 만나요"
			);

		assertThat(curationFeed.getBakeries().getBakeries().size()).isEqualTo(1);
		assertThat(curationFeed.getBakeries().getBakeries().get(0).getBakery().getId()).isEqualTo(3);
	}

	@DisplayName("피드에 좋아요 된 총 카운트를 계산한다")
	@Test
	void 피드_좋아요_총_카운트_계산_테스트() {
		//given

		//when

		//then
	}

	@DisplayName("한 사람이 피드에 좋아요 한 카운트를 계산한다")
	@Test
	void 피드_좋아요_유저_개별_카운트_계산_테스트() {
		//given

		//when

		//then
	}

	@DisplayName("피드에 좋아요를 할 수 있다")
	@Test
	void 피드_좋아요_테스트() {
		//given

		//when

		//then
	}

	@DisplayName("피드에 좋아요를 할 수 있다 - 좋아요 개수가 5개 이상이라면 실패")
	@Test
	void 피드_좋아요_테스트_5개_이상_실패() {
		//given

		//when

		//then
	}

	@DisplayName("피드에 좋아요를 취소할 수 있다")
	@Test
	void 피드_좋아요_취소_테스트() {
		//given

		//when

		//then
	}

	@DisplayName("피드에 좋아요를 취소할 수 있다 - 좋아요 한 이력이 없다면 실패")
	@Test
	void 피드_좋아요_취소_테스트_좋아요_이력_없으면_실패() {
		//given

		//when

		//then
	}

	@DisplayName("피드에 좋아요를 취소할 수 있다 - 좋아요가 0개라면 실패")
	@Test
	void 피드_좋아요_취소_테스트_좋아요_0개면_실패() {
		//given

		//when

		//then
	}

	@DisplayName("유저가 피드에 좋아요한 이력을 찾는다")
	@Test
	void 피드_좋아요_이력_찾기() {
		//given

		//when

		//then
	}
}
