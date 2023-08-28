package com.depromeet.breadmapbackend.domain.admin.feed.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.category.repository.CategoryRepository;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.LandingFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedSearchRequest;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForAdmin;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForUser;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.utils.TestQueryDslConfig;

@DataJpaTest
@Import(TestQueryDslConfig.class)
public class FeedRepositoryTest_Search {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	LandingFeedRepository landingFeedRepository;

	@Autowired
	CurationFeedRepository curationFeedRepository;

	@Autowired
	AdminRepository adminRepository;

	@Autowired
	FeedRepository feedRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	BakeryRepository bakeryRepository;

	@DisplayName("검색 조건에 맞는 피드 정보를 가져온다 - 관리자")
	@ParameterizedTest
	@MethodSource("getFeedForSearchFeedByActiveAtAndCreateByAndActivatedAndCategoryName")
	void 검색조건에_따라_피드를_조회한다(
		String keyword,
		List<Admin> admins,
		List<Category> categories,
		List<Feed> feeds,
		FeedResponseForAdmin expected
	) {
		//given
		adminRepository.saveAll(admins);
		categoryRepository.saveAll(categories);
		feedRepository.saveAll(feeds);

		flushAndClear();

		FeedSearchRequest searchRequest = findSearchRequestByKeyword(keyword);

		//when
		Page<Feed> content = feedRepository.findAllFeedBySearch(PageRequest.of(0, 10), searchRequest);

		FeedResponseForAdmin actual = FeedAssembler.toDtoForAdmin(content.getTotalPages(), content.getTotalElements(),
			content.getContent());

		//then
		assertThat(actual)
			.usingRecursiveComparison()
			.ignoringFields("contents.feedId", "contents.createdAt")
			.isEqualTo(expected);
	}

	@DisplayName("POSTING 상태이며 게시일자가 지났을 경우 피드를 종류에 상관없이 10개까지 조회한다 : 유저")
	@Test
	void 종류에_상관없이_POSTING_상태의_피드를_조회한다_게시일자_역순으로_최대10개() {
		//given
		Admin admin = Admin.builder()
			.email("test1@ddb.com")
			.password("1234")
			.build();

		Category category = Category.builder().categoryName("대동빵 멤버 추천 빵집").build();

		List<Feed> feeds = List.of(
			LandingFeed.builder()
				.admin(admin)
				.category(category)
				.redirectUrl("testRedirect1")
				.feedType(FeedType.LANDING.toString())
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.POSTING)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0))
				.build(),
			LandingFeed.builder()
				.admin(admin)
				.category(category)
				.redirectUrl("testRedirect2")
				.feedType(FeedType.LANDING.toString())
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.POSTING)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 1))
				.build(),
			LandingFeed.builder()
				.admin(admin)
				.category(category)
				.redirectUrl("testRedirect3")
				.feedType(FeedType.LANDING.toString())
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.INACTIVATED)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2030, 1, 1, 0, 0, 0))
				.build(),
			CurationFeed.builder()
				.admin(admin)
				.category(category)
				.subTitle("testTitle1")
				.introduction("서론1")
				.conclusion("결론1")
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.INACTIVATED)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0))
				.build(),
			CurationFeed.builder()
				.admin(admin)
				.category(category)
				.subTitle("testTitle2")
				.introduction("서론2")
				.conclusion("결론2")
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.POSTING)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
				.build(),
			CurationFeed.builder()
				.admin(admin)
				.category(category)
				.subTitle("testTitle3")
				.introduction("서론3")
				.conclusion("결론3")
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.POSTING)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2030, 1, 1, 0, 0, 0))
				.build()
		);

		adminRepository.save(admin);
		categoryRepository.save(category);
		List<Feed> expectedContent = feedRepository.saveAll(feeds);

		List<Long> expected = List.of(
			expectedContent.get(1).getId(),
			expectedContent.get(4).getId(),
			expectedContent.get(0).getId()
		);

		flushAndClear();

		//when
		List<Feed> actualContent = feedRepository.getAllFeedForUser();

		List<Long> actual = FeedAssembler.toDtoForUser(actualContent).stream()
			.map(FeedResponseForUser::getFeedId)
			.collect(Collectors.toList());

		//then
		assertThat(actual.size()).isEqualTo(3);
		assertThat(actual).containsExactly(expected.get(0),
			expected.get(1),
			expected.get(2));
	}

	public static Stream<Arguments> getFeedForSearchFeedByActiveAtAndCreateByAndActivatedAndCategoryName() {
		return Stream.of(
			createArgument("all", 0, 1, 2, 3, 4, 5),
			createArgument("Only Posting", 0, 1, 4, 5),
			createArgument("createBy admin1", 0, 2, 4),
			createArgument("categoryName 대동빵 멤버 추천 빵집", 1, 3, 5)
		);
	}

	private static Arguments createArgument(String keyword, int... expectedIndex) {
		Admin admin1 = Admin.builder()
			.email("test1@ddb.com")
			.password("1234")
			.build();

		Admin admin2 = Admin.builder()
			.email("test2@ddb.com")
			.password("1234")
			.build();

		Category category1 = Category.builder().categoryName("대동빵 멤버 추천 빵집").build();
		Category category2 = Category.builder().categoryName("8월 빵집").build();

		Feed[] feeds = new Feed[] {
			LandingFeed.builder()
				.admin(admin1)
				.category(category2)
				.redirectUrl("testRedirect1")
				.feedType(FeedType.LANDING.toString())
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.POSTING)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0))
				.build(),
			LandingFeed.builder()
				.admin(admin2)
				.category(category1)
				.redirectUrl("testRedirect2")
				.feedType(FeedType.LANDING.toString())
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.POSTING)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
				.build(),
			LandingFeed.builder()
				.admin(admin1)
				.category(category2)
				.redirectUrl("testRedirect3")
				.feedType(FeedType.LANDING.toString())
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.INACTIVATED)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2030, 1, 1, 0, 0, 0))
				.build(),
			CurationFeed.builder()
				.admin(admin2)
				.category(category1)
				.subTitle("testTitle1")
				.introduction("서론1")
				.conclusion("결론1")
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.INACTIVATED)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0))
				.build(),
			CurationFeed.builder()
				.admin(admin1)
				.category(category2)
				.subTitle("testTitle2")
				.introduction("서론2")
				.conclusion("결론2")
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.POSTING)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
				.build(),
			CurationFeed.builder()
				.admin(admin2)
				.category(category1)
				.subTitle("testTitle3")
				.introduction("서론3")
				.conclusion("결론3")
				.thumbnailUrl("https://www.naver.com")
				.activated(FeedStatus.POSTING)
				.feedType(FeedType.CURATION.toString())
				.activeTime(LocalDateTime.of(2030, 1, 1, 0, 0, 0))
				.build()
		};

		List<Feed> expected = Arrays.stream(expectedIndex)
			.mapToObj(idx -> feeds[idx])
			.toList();

		PageImpl<Feed> page = new PageImpl<>(expected);

		return Arguments.of(
			keyword,
			List.of(admin1, admin2),
			List.of(category1, category2),
			List.of(feeds[0], feeds[1], feeds[2], feeds[3], feeds[4], feeds[5]),
			FeedAssembler.toDtoForAdmin(page.getTotalPages(), page.getTotalElements(), expected)
		);
	}

	private FeedSearchRequest findSearchRequestByKeyword(String keyword) {
		return switch (keyword) {
			case "all" -> new FeedSearchRequest();
			case "Only Posting" -> new FeedSearchRequest(null, null, FeedStatus.POSTING, null);
			case "createBy admin1" -> new FeedSearchRequest(null, "test1@ddb.com", null, null);
			case "categoryName 대동빵 멤버 추천 빵집" -> new FeedSearchRequest(null, null, null, "대동빵 멤버 추천 빵집");
			default -> throw new IllegalStateException("테스트 실패");
		};
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}
