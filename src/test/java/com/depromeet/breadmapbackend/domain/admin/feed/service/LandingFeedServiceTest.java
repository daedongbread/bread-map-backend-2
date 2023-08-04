package com.depromeet.breadmapbackend.domain.admin.feed.service;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.category.repository.CategoryRepository;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.LandingFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CommonFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.LandingFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.CommonFeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.LandingFeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.repository.LandingFeedRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LandingFeedServiceTest {

    @InjectMocks
    private LandingFeedService landingFeedService;

    @Mock
    private LandingFeedRepository landingFeedRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private static MockedStatic<FeedAssembler> testAssembler;

    @BeforeAll
    public static void beforeALl() {
        testAssembler = mockStatic(FeedAssembler.class);
    }

    @AfterAll
    public static void afterAll() {
        testAssembler.close();
    }

    @DisplayName("랜딩 피드를 등록한다")
    @Test
    void 랜딩_피드_등록_테스트() {

        //given
        Admin admin = Admin.builder().id(1L).email("test admin").password("1234").build();
        Category category = Category.builder().id(1L).categoryName("test category").build();

        CommonFeedRequestDto commonLanding = CommonFeedRequestDto.builder()
                .categoryId(category.getId())
                .thumbnailUrl("test thumbnail image")
                .activated(FeedStatus.POSTING)
                .feedType(FeedType.LANDING)
                .activeTime("2023-07-01T00:00:00")
                .build();

        LandingFeedRequestDto landingDto = new LandingFeedRequestDto("www.test.com");

        FeedRequestDto requestDto = new FeedRequestDto(commonLanding, landingDto);

        LandingFeed expected = LandingFeed.builder()
                .id(1L)
                .feedType(FeedType.LANDING.toString())
                .category(category)
                .admin(admin)
                .redirectUrl("www.test.com")
                .activated(FeedStatus.POSTING)
                .activeTime(LocalDateTime.of(2023, 7, 1, 0, 0, 0))
                .thumbnailUrl("test thumbnail image")
                .build();

        given(adminRepository.findById(anyLong()))
                .willReturn(Optional.of(admin));
        given(categoryRepository.findById(anyLong()))
                .willReturn(Optional.of(category));
        given(FeedAssembler.toEntity(any(Admin.class), any(Category.class), any(FeedRequestDto.class)))
                .willReturn(expected);
        given(landingFeedRepository.save(any(LandingFeed.class)))
                .willReturn(expected);

        //when
        Long actualId = landingFeedService.addFeed(admin.getId(), requestDto);

        //then
        assertThat(actualId).isNotNull();
        assertThat(actualId).isEqualTo(expected.getId());

        verify(adminRepository, times(1))
                .findById(expected.getId());
        verify(categoryRepository, times(1))
                .findById(expected.getCategory().getId());
        verify(landingFeedRepository, times(1))
                .save(expected);
    }

    @DisplayName("랜딩 피드를 수정한다")
    @Test
    void 랜딩_피드_수정_테스트() {

        //given
        Admin admin = Admin.builder().id(1L).email("test admin").password("1234").build();
        Category category = Category.builder().id(1L).categoryName("test category").build();

        LandingFeed asIs = LandingFeed.builder()
                .id(1L)
                .feedType(FeedType.LANDING.toString())
                .category(category)
                .admin(admin)
                .redirectUrl("www.test.com")
                .activated(FeedStatus.POSTING)
                .activeTime(LocalDateTime.of(2023, 7, 1, 0, 0, 0))
                .thumbnailUrl("test thumbnail image")
                .build();

        CommonFeedRequestDto commonLanding = CommonFeedRequestDto.builder()
                .categoryId(category.getId())
                .thumbnailUrl("change thumbnail image")
                .activated(FeedStatus.INACTIVATED)
                .feedType(FeedType.LANDING)
                .activeTime("2023-07-01T00:00:00")
                .build();

        LandingFeedRequestDto landingDto = new LandingFeedRequestDto("changeRedirectUrl");

        FeedRequestDto updateRequestDto = new FeedRequestDto(commonLanding, landingDto);

        LandingFeed toBe = LandingFeed.builder()
                .id(1L)
                .feedType(FeedType.LANDING.toString())
                .category(category)
                .admin(admin)
                .redirectUrl("changeRedirectUrl")
                .activated(FeedStatus.INACTIVATED)
                .activeTime(LocalDateTime.of(2023, 7, 1, 0, 0, 0))
                .thumbnailUrl("change thumbnail image")
                .build();

        given(adminRepository.findById(anyLong()))
                .willReturn(Optional.of(admin));
        given(landingFeedRepository.findById(anyLong()))
                .willReturn(Optional.of(asIs));
        given(categoryRepository.findById(anyLong()))
                .willReturn(Optional.of(category));
        given(FeedAssembler.toEntity(any(Admin.class), any(Category.class), any(FeedRequestDto.class)))
                .willReturn(toBe);

        //when
        landingFeedService.updateFeed(admin.getId(), asIs.getId(), updateRequestDto);

        //then
        assertThat(asIs).extracting(
                "redirectUrl", "thumbnailUrl", "activated"
        )
                .containsExactly(
                        landingDto.getRedirectUrl(), commonLanding.getThumbnailUrl(), commonLanding.getActivated()
                );
    }

    @DisplayName("랜딩 피드를 조회한다")
    @Test
    void 랜딩_피드_조회_테스트() {

        //given
        Long feedId = 1L;

        LandingFeed expected = LandingFeed.builder()
                .id(feedId)
                .redirectUrl("www.test.com")
                .activeTime(LocalDateTime.now())
                .activated(FeedStatus.POSTING)
                .category(Category.builder().categoryName("test category").build())
                .build();

        CommonFeedResponseDto common = CommonFeedResponseDto.builder()
                .feedId(expected.getId())
                .activated(expected.getActivated().toString())
                .categoryName(expected.getCategory().getCategoryName())
                .activateTime(expected.getActiveTime().toString())
                .build();

        LandingFeedResponseDto landing = new LandingFeedResponseDto(expected.getRedirectUrl());

        given(landingFeedRepository.findById(anyLong()))
                .willReturn(Optional.of(expected));
        given(FeedAssembler.toCommonDto(any(Feed.class)))
                .willReturn(common);
        given(FeedAssembler.toLandingDto(any(Feed.class)))
                .willReturn(landing);

        //when
        FeedResponseDto actual = landingFeedService.getFeed(feedId);

        //then
        assertThat(actual.getCommon().getFeedId()).isEqualTo(expected.getId());
        assertThat(actual.getLanding().getRedirectUrl()).isEqualTo(expected.getRedirectUrl());

        verify(landingFeedRepository, times(1))
                .findById(feedId);
    }
}
