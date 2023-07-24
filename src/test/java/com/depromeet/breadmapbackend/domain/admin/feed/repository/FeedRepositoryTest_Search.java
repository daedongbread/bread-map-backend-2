//package com.depromeet.breadmapbackend.domain.admin.feed.repository;
//
//import com.depromeet.breadmapbackend.domain.admin.Admin;
//import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
//import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
//import com.depromeet.breadmapbackend.domain.admin.category.repository.CategoryRepository;
//import com.depromeet.breadmapbackend.domain.admin.feed.domain.*;
//import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
//import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
//import com.depromeet.breadmapbackend.utils.TestQueryDslConfig;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.context.annotation.Import;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Stream;
//
//@DataJpaTest
//@Import(TestQueryDslConfig.class)
//public class FeedRepositoryTest_Search {
//
//    @Autowired
//    TestEntityManager entityManager;
//
//    @Autowired
//    LandingFeedRepository landingFeedRepository;
//
//    @Autowired
//    CurationFeedRepository curationFeedRepository;
//
//    @Autowired
//    AdminRepository adminRepository;
//
//    @Autowired
//    CategoryRepository categoryRepository;
//
//    @Autowired
//    BakeryRepository bakeryRepository;
//
//    @DisplayName("검색 조건에 맞는 피드 정보를 가져온다 : 관리자")
//    @ParameterizedTest
//    @MethodSource("getFeedForSearchFeedByActiveAtAndCreateByAndActivatedAndCategoryName")
//    void 검색조건에_따라_피드를_조회한다() {
//        //given
//
//        //when
//
//        //then
//    }
//
//    @DisplayName("POSTING 상태이며 게시일자가 지났을 경우 피드를 종류에 상관없이 10개까지 조회한다 : 유저")
//    @Test
//    void 종류에_상관없이_POSTING_상태의_피드를_조회한다_게시일자_역순으로_최대10개() {
//        //given
//
//        //when
//
//        //then
//    }
//
//    public static Stream<Arguments> getFeedForSearchFeedByActiveAtAndCreateByAndActivatedAndCategoryName() {
//        return Stream.of(
//                createArgument("all"),
//                createArgument("activeAt null"),
//                createArgument("createBy null"),
//                createArgument("activated null"),
//                createArgument("categoryName null")
//        );
//    }
//
//    private static Arguments createArgument(String keyword, int... expectedIndex) {
//        Admin admin = Admin.builder()
//                .email("test@ddb.com")
//                .password("1234")
//                .build();
//
//        Category category = new Category("testCategory");
//
//        LandingFeed[] lfs = new LandingFeed[]{
//                LandingFeed.builder()
//                        .admin(admin)
//                        .category(category)
//                        .redirectUrl("testRedirect1")
//                        .feedType(FeedType.LANDING.toString())
//                        .thumbnailUrl("https://www.naver.com")
//                        .activated(FeedStatus.POSTING)
//                        .feedType(FeedType.CURATION.toString())
//                        .activeTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0))
//                        .build(),
//                LandingFeed.builder()
//                        .admin(admin)
//                        .category(category)
//                        .redirectUrl("testRedirect2")
//                        .feedType(FeedType.LANDING.toString())
//                        .thumbnailUrl("https://www.naver.com")
//                        .activated(FeedStatus.POSTING)
//                        .feedType(FeedType.CURATION.toString())
//                        .activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
//                        .build(),
//                LandingFeed.builder()
//                        .admin(admin)
//                        .category(category)
//                        .redirectUrl("testRedirect3")
//                        .feedType(FeedType.LANDING.toString())
//                        .thumbnailUrl("https://www.naver.com")
//                        .activated(FeedStatus.INACTIVATED)
//                        .feedType(FeedType.CURATION.toString())
//                        .activeTime(LocalDateTime.of(2030, 1, 1, 0, 0, 0))
//                        .build()
//        };
//
//        CurationFeed[] cfs = new CurationFeed[]{
//                CurationFeed.builder()
//                        .admin(admin)
//                        .category(category)
//                        .subTitle("testTitle1")
//                        .introduction("서론1")
//                        .conclusion("결론1")
//                        .thumbnailUrl("https://www.naver.com")
//                        .activated(FeedStatus.INACTIVATED)
//                        .feedType(FeedType.CURATION.toString())
//                        .activeTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0))
//                        .build(),
//                CurationFeed.builder()
//                        .admin(admin)
//                        .category(category)
//                        .subTitle("testTitle2")
//                        .introduction("서론2")
//                        .conclusion("결론2")
//                        .thumbnailUrl("https://www.naver.com")
//                        .activated(FeedStatus.POSTING)
//                        .feedType(FeedType.CURATION.toString())
//                        .activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
//                        .build(),
//                CurationFeed.builder()
//                        .admin(admin)
//                        .category(category)
//                        .subTitle("testTitle3")
//                        .introduction("서론3")
//                        .conclusion("결론3")
//                        .thumbnailUrl("https://www.naver.com")
//                        .activated(FeedStatus.POSTING)
//                        .feedType(FeedType.CURATION.toString())
//                        .activeTime(LocalDateTime.of(2030, 1, 1, 0, 0, 0))
//                        .build()
//        };
//
//        return Arguments.of(
//                keyword,
//                admin,
//                category,
//                List.of(posts[0], posts[1], posts[2]),
//                List.of(tags),
//                FeedAssembler.postResponseDtos(user, expected)
//        );
//    }
//}
