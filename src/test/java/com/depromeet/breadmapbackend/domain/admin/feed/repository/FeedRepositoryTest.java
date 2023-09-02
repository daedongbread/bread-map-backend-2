package com.depromeet.breadmapbackend.domain.admin.feed.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.category.repository.CategoryRepository;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationBakery;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.LandingFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CommonFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CurationFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.utils.TestQueryDslConfig;

@DataJpaTest
@Import(TestQueryDslConfig.class)
public class FeedRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	LandingFeedRepository landingFeedRepository;

	@Autowired
	CurationFeedRepository curationFeedRepository;

	@Autowired
	AdminRepository adminRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	BakeryRepository bakeryRepository;
	private List<String> images = List.of("test images");

	@DisplayName("랜딩 피드를 저장한다")
	@Test
	void 랜딩_피드를_저장한다() {
		//given
		Admin admin = adminRepository.save(Admin.builder()
			.email("test@ddb.com")
			.password("1234")
			.build());

		Category category = categoryRepository.save(Category.builder().categoryName("testCategory").build());

		LandingFeed feed = LandingFeed.builder()
			.admin(admin)
			.category(category)
			.redirectUrl("testRedirectUrl")
			.feedType(FeedType.LANDING.toString())
			.build();

		//when
		LandingFeed landingFeed = landingFeedRepository.save(feed);
		flushAndClear();

		//then
		LandingFeed actual = landingFeedRepository.findById(feed.getId()).orElse(null);

		assertThat(actual.getId()).isEqualTo(landingFeed.getId());
		assertThat(actual)
			.extracting("redirectUrl", "admin.email", "category.categoryName", "feedType")
			.contains("testRedirectUrl", "test@ddb.com", "testCategory", "LANDING");
	}

	@DisplayName("큐레이션 피드를 저장한다")
	@Test
	void 큐레이션_피드를_저장한다() {
		//given
		Admin admin = adminRepository.save(Admin.builder()
			.email("test@ddb.com")
			.password("1234")
			.build());

		Category category = categoryRepository.save(Category.builder().categoryName("testCategory").build());

		CurationFeed feed = CurationFeed.builder()
			.admin(admin)
			.category(category)
			.feedType(FeedType.CURATION.toString())
			.subTitle("8월 추천 빵집")
			.introduction("서론")
			.conclusion("결론")
			.build();

		//when
		CurationFeed curationFeed = curationFeedRepository.save(feed);
		flushAndClear();

		//then
		CurationFeed actual = curationFeedRepository.findById(feed.getId()).orElse(null);

		assertThat(actual.getId()).isEqualTo(curationFeed.getId());
		assertThat(actual)
			.extracting("subTitle", "introduction", "conclusion", "feedType")
			.contains("8월 추천 빵집", "서론", "결론", "CURATION");
	}

	@DisplayName("큐레이션에 추천빵집을 추가할 경우, 큐레이션을 저장할 때 함께 저장된다")
	@Test
	void 큐레이션_피드를_저장할_경우_추천빵집이_함께_저장된다() {
		//given
		Admin admin = adminRepository.save(Admin.builder()
			.email("test@ddb.com")
			.password("1234")
			.build());

		Category category = categoryRepository.save(Category.builder().categoryName("testCategory").build());

		Bakery bakery = bakeryRepository.save(Bakery.builder()
			.address("address")
			.latitude(37.5596080725671)
			.longitude(127.044235133983)
			.name("test bakery 1")
			.status(BakeryStatus.POSTING)
			.images(new ArrayList<>(images))
			.build());

		CurationFeed feed = CurationFeed.builder()
			.admin(admin)
			.category(category)
			.feedType(FeedType.CURATION.toString())
			.subTitle("8월 추천 빵집")
			.activeTime(LocalDateTime.now())
			.introduction("안녕하세요")
			.conclusion("다음에 또 만나요")
			.activated(FeedStatus.POSTING)
			.thumbnailUrl("testUrl")
			.build();

		FeedRequestDto request = new FeedRequestDto(
			CommonFeedRequestDto.builder()
				.feedType(FeedType.CURATION)
				.subTitle("8월 추천 빵집")
				.activeTime("2023-07-025T00:00:00")
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
					.build()));

		List<CurationBakery> curationBakeries = FeedAssembler.toCurationBakery(feed, List.of(bakery), request);

		feed.addAll(List.of(bakery), curationBakeries);

		//when
		CurationFeed curationFeed = curationFeedRepository.save(feed);
		flushAndClear();

		//then
		CurationFeed actual = curationFeedRepository.findById(curationFeed.getId()).orElse(null);

		assertThat(actual).isNotNull();
		assertThat(actual.getBakeries().getCurationBakeries().size()).isEqualTo(1);
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}
