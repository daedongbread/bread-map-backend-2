package com.depromeet.breadmapbackend.domain.community.controller;

import com.depromeet.breadmapbackend.domain.community.application.CommunityFacade;
import com.depromeet.breadmapbackend.domain.community.controller.dto.CommunityWithOutProductsCreateRequest;
import com.depromeet.breadmapbackend.domain.community.controller.dto.CommunityWithProductsCreateRequest;
import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * CommunityController
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityFacade communityFacade;

    // 커뮤니티 등록
    @PostMapping("/v1/community") // /v1/posts
    @ResponseStatus(HttpStatus.CREATED)
    void registerCommunityWithOutProducts(
        @AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
        @RequestBody @Valid final CommunityWithOutProductsCreateRequest request
    ) {
        validatePostTopic(request.communityType());
        communityFacade.registerCommunityWithOutProducts(currentUserInfo.getId(), request);
    }

    // 커뮤니티 리뷰 등록
    @PostMapping("/v1/community/products") // /v1/posts
    @ResponseStatus(HttpStatus.CREATED)
    void registerCommunityWithProducts(
        @AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
        @RequestBody @Valid final CommunityWithProductsCreateRequest request
    ) {
        validatePostTopic(request.communityType());
        communityFacade.registerCommunityWithProducts(currentUserInfo.getId(), request);
    }
    //
    // // post 상세 조회
    // @GetMapping("/v1/community/{communityType}/{communityId}") // /v1/posts/{postTopic}/{postId}
    // @ResponseStatus(HttpStatus.OK)
    // ApiResponse<PostResponse> getPost(
    // 	@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
    // 	@PathVariable("communityId") final Long postId,
    // 	@PathVariable("communityType") final String postTopic
    // ) {
    // 	final PostTopic topic = PostTopic.of(postTopic);
    // 	return new ApiResponse<>(
    // 		Mapper.of(postService.getDetailPost(postId, currentUserInfo.getId(), topic))
    // 	);
    // }
    //
    // // post 전체 조회
    // @GetMapping("/v1/community/cards/{communityType}") // /v1/posts/cards/{postTopic}
    // @ResponseStatus(HttpStatus.OK)
    // ApiResponse<PageCommunityResponseDto<CommunityCardResponse>> getCommunityCards(
    // 	@PathVariable("communityType") final String communityType,
    // 	@RequestParam(value = "reviewOffset", required = false, defaultValue = "0") long reviewOffset,
    // 	@RequestParam(value = "postOffset", required = false, defaultValue = "0") long postOffset,
    // 	@RequestParam(value = "page", required = false, defaultValue = "0") int page,
    // 	@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
    // ) {
    // 	final PostTopic topic = PostTopic.of(postTopic);
    // 	return new ApiResponse<>(
    // 		postService.getCommunityCards(new CommunityPage(reviewOffset, postOffset, topic, page),
    // 			currentUserInfo.getId()));
    // }
    //
    // // post 추천글 조회
    // @GetMapping("/v1/community/hot") // /v1/posts/hot
    // @ResponseStatus(HttpStatus.OK)
    // ApiResponse<List<CommunityCardResponse>> findHotCommunityPosts(
    // 	@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
    // ) {
    // 	return new ApiResponse<>(postService.findHotPosts(currentUserInfo.getId()));
    // }
    //
    // // post 삭제
    // @DeleteMapping("/v1/community/{communityType}/{communityId}")  // /v1/posts/{postTopic}/{postId}
    // @ResponseStatus(HttpStatus.OK)
    // void remove(
    // 	@PathVariable("communityId") final Long postId,
    // 	@PathVariable("communityType") final String postTopic,
    // 	@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
    // ) {
    // 	final PostTopic topic = PostTopic.of(postTopic);
    // 	validatePostTopic(topic);
    // 	postService.delete(postId, topic, currentUserInfo.getId());
    // }
    //
    // // post 수정
    // @PutMapping("/v1/community/{communityType}/{communityId}")  // /v1/posts/{postId}
    // @ResponseStatus(HttpStatus.OK)
    // void update(
    // 	@PathVariable("communityId") final Long postId,
    // 	@PathVariable("communityType") final String postTopic,
    // 	@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
    // 	@RequestBody @Valid final PostRequest request
    // ) {
    // 	validatePostTopic(request.postTopic());
    // 	postService.update(currentUserInfo.getId(), Mapper.of(request, postId));
    // }
    //
    // // post 좋아요 토글
    // @PostMapping("/v1/community/like/{communityType}/{communityId}")  // /v1/posts/like/{postId}
    // @ResponseStatus(HttpStatus.OK)
    // ApiResponse<Integer> toggle(
    // 	@PathVariable("communityId") final Long postId,
    // 	@PathVariable("communityType") final String postTopic,
    // 	@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
    // ) {
    // 	return new ApiResponse<>(postService.toggle(postId, currentUserInfo.getId()));
    // }

    private void validatePostTopic(final CommunityType communityType) {
        if (communityType == CommunityType.EVENT) {
            throw new DaedongException(DaedongStatus.INVALID_COMMUNITY_TYPE);
        }
    }
}
