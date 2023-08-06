package com.depromeet.breadmapbackend.domain.admin.feed.service;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.dto.FeedLikeResponse;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.*;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedSearchRequest;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForAdmin;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForUser;
import com.depromeet.breadmapbackend.domain.admin.feed.repository.FeedRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommonFeedServiceTest {

    @InjectMocks
    CommonFeedService commonFeedService;

    @Mock
    UserRepository userRepository;

    @Mock
    FeedRepository feedRepository;

    @DisplayName("어드민용 Response로 모든 피드를 조회한다. - 검색과 페이징 조건에 맞게 조회")
    @Test
    void 피드_전체조회_테스트_어드민_페이징() {
        //given
        Admin admin = Admin.builder()
                .email("test@ddb.com")
                .password("1234")
                .build();

        List<Feed> feeds = List.of(
                LandingFeed.builder()
                        .id(1L)
                        .admin(admin)
                        .category(new Category("hello"))
                        .feedType(FeedType.LANDING.toString())
                        .activated(FeedStatus.POSTING)
                        .activeTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0))
                        .build(),
                LandingFeed.builder()
                        .id(2L)
                        .admin(admin)
                        .category(new Category("world"))
                        .feedType(FeedType.LANDING.toString())
                        .activated(FeedStatus.POSTING)
                        .activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                        .build(),
                CurationFeed.builder()
                        .id(3L)
                        .admin(admin)
                        .category(new Category("helloWorld"))
                        .subTitle("testTitle2")
                        .activated(FeedStatus.POSTING)
                        .feedType(FeedType.CURATION.toString())
                        .activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 5);

        FeedSearchRequest searchRequest = new FeedSearchRequest(null, null, FeedStatus.POSTING, "hello");

        List<Feed> expected = List.of(feeds.get(0), feeds.get(2));

        given(feedRepository.findAllFeedBySearch(any(Pageable.class), any(FeedSearchRequest.class)))
                .willReturn(new PageImpl<>(expected, pageable, expected.size()));

        //when
        FeedResponseForAdmin actual = commonFeedService.getAllFeedForAdmin(pageable, searchRequest);

        //then
        assertThat(actual.getContents()).
            extracting(
                "feedId", "categoryName", "isActive"
            ).
            containsExactly(
                tuple(expected.get(0).getId(), expected.get(0).getCategory().getCategoryName(), expected.get(0).getActivated() == FeedStatus.POSTING ? "게시중" : "미게시"),
                tuple(expected.get(1).getId(), expected.get(1).getCategory().getCategoryName(), expected.get(1).getActivated() == FeedStatus.POSTING ? "게시중" : "미게시")
        );
    }

    @DisplayName("유저용 Response로 모든 피드를 조회한다.")
    @Test
    void 피드_전체조회_테스트_유저() {
        //given
        List<Feed> feeds = List.of(
                LandingFeed.builder()
                        .id(1L)
                        .feedType(FeedType.LANDING.toString())
                        .activated(FeedStatus.POSTING)
                        .activeTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0))
                        .build(),
                LandingFeed.builder()
                        .id(2L)
                        .feedType(FeedType.LANDING.toString())
                        .activated(FeedStatus.POSTING)
                        .activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                        .build(),
                CurationFeed.builder()
                        .id(3L)
                        .subTitle("testTitle2")
                        .activated(FeedStatus.POSTING)
                        .feedType(FeedType.CURATION.toString())
                        .activeTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                        .build()
        );

        given(feedRepository.getAllFeedForUser())
                .willReturn(feeds);

        //when
        List<FeedResponseForUser> actual = commonFeedService.getAllFeedForUser();

        //then
        assertThat(actual).extracting(
                        "feedId", "feedType"
                )
                .contains(
                        tuple(feeds.get(0).getId(), feeds.get(0).getFeedType()),
                        tuple(feeds.get(1).getId(), feeds.get(1).getFeedType()),
                        tuple(feeds.get(2).getId(), feeds.get(2).getFeedType())
                );

        verify(feedRepository, times(1))
                .getAllFeedForUser();
    }

    @DisplayName("유저는 피드에 좋아요를 누를 수 있다. - 성공")
    @Test
    void 유저_좋아요_테스트_성공() {
        //given
        User user = User.builder().id(1L).build();
        Long feedId = 1L;

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(feedRepository.findById(anyLong()))
                .willReturn(Optional.of(CurationFeed
                        .builder()
                        .id(feedId)
                        .subTitle("test feed")
                        .build()));

        //when
        FeedLikeResponse feedLikeResponse = commonFeedService.likeFeed(user.getId(), feedId);

        //then
        assertThat(feedLikeResponse.getLikeCounts()).isEqualTo(1);
        assertThat(feedLikeResponse.getLikeStatus()).isEqualTo("LIKE");

        verify(userRepository, times(1))
                .findById(user.getId());
        verify(feedRepository, times(1))
                .findById(feedId);
    }

    @DisplayName("유저는 피드에 좋아요를 누를 수 있다. - 1인당 좋아요가 5개 이상이면 실패")
    @Test
    void 유저_좋아요_테스트_1인당_5개_이상이면_실패_500Exception() {
        //given
        User user = User.builder().id(1L).build();
        Long feedId = 1L;

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(feedRepository.findById(anyLong()))
                .willReturn(Optional.of(CurationFeed
                        .builder()
                        .id(feedId)
                        .subTitle("test feed")
                        .build()));

        //when
        for(int idx = 1 ; idx <= 5 ; idx++) {
            commonFeedService.likeFeed(user.getId(), feedId);
        }

        //then
        assertThatThrownBy(() ->  {
            commonFeedService.likeFeed(user.getId(), feedId);
        })
                .isInstanceOf(DaedongException.class)
                .hasFieldOrPropertyWithValue("daedongStatus.code", 50002)
                .hasFieldOrPropertyWithValue("daedongStatus.status", HttpStatus.INTERNAL_SERVER_ERROR);

        verify(userRepository, times(6))
                .findById(user.getId());
        verify(feedRepository, times(6))
                .findById(feedId);
    }

    @DisplayName("유저는 피드에 좋아요를 누를 수 있다. - 피드 없으면 실패 - 404예외")
    @Test
    void 유저_좋아요_테스트_피드_없으면_실패_404Exception() {
        //given
        User user = User.builder().id(1L).build();
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(feedRepository.findById(anyLong()))
                .willThrow(new DaedongException(DaedongStatus.FEED_NOT_FOUND));

        //when then
        assertThatThrownBy(() -> {
            commonFeedService.likeFeed(1L, 1L);
        })
                .isInstanceOf(DaedongException.class)
                .hasFieldOrPropertyWithValue("daedongStatus.code", 40453)
                .hasFieldOrPropertyWithValue("daedongStatus.status", HttpStatus.NOT_FOUND);

        verify(userRepository, times(1))
                .findById(user.getId());
        verify(feedRepository, times(1))
                .findById(1L);
    }

    @DisplayName("유저는 피드에 좋아요를 누를 수 있다. - 유저 없으면 실패 - 404예외")
    @Test
    void 유저_좋아요_테스트_유저_못찾으면_실패_404Exception() {
        //given
        given(userRepository.findById(anyLong()))
                .willThrow(new DaedongException(DaedongStatus.USER_NOT_FOUND));

        //when then
        assertThatThrownBy(() -> {
            commonFeedService.likeFeed(1L, 1L);
        })
                .isInstanceOf(DaedongException.class)
                .hasFieldOrPropertyWithValue("daedongStatus.code", 40410)
                .hasFieldOrPropertyWithValue("daedongStatus.status", HttpStatus.NOT_FOUND);

        verify(userRepository, times(1))
                .findById(1L);
    }
}
