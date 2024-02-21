package com.depromeet.breadmapbackend.domain.community.controller;

import com.depromeet.breadmapbackend.domain.community.controller.dto.CommunityWithOutProductsCreateRequest;
import com.depromeet.breadmapbackend.domain.community.controller.dto.CommunityWithProductsCreateRequest;
import com.depromeet.breadmapbackend.domain.community.controller.dto.NoExistProductRatingRequest;
import com.depromeet.breadmapbackend.domain.community.controller.dto.ProductRatingRequest;
import com.depromeet.breadmapbackend.domain.community.domain.dto.CommunityWithOutProductsRegisterCommand;
import com.depromeet.breadmapbackend.domain.community.domain.dto.CommunityWithProductsRegisterCommand;
import com.depromeet.breadmapbackend.domain.community.domain.dto.NoExistProductRatingCommand;
import com.depromeet.breadmapbackend.domain.community.domain.dto.ProductRatingCommand;
import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostUpdateCommand;
import com.depromeet.breadmapbackend.domain.post.dto.request.PostRequest;
import com.depromeet.breadmapbackend.domain.post.dto.response.PostResponse;

/**
 * Mapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
public class Mapper {

    public static CommunityWithOutProductsRegisterCommand of(
        final CommunityWithOutProductsCreateRequest request) {
        return new CommunityWithOutProductsRegisterCommand(
            request.title(),
            request.content(),
            request.images(),
            request.communityType(),
            request.bakeryId()
        );
    }

    public static CommunityWithProductsRegisterCommand of(final CommunityWithProductsCreateRequest request) {
        return new CommunityWithProductsRegisterCommand(
            request.content(),
            request.images(),
            request.communityType(),
            request.bakeryId(),
            request.noExistProductRatingRequestList().stream()
                .map(Mapper::of)
                .toList(),
            request.productRatingList().stream()
                .map(Mapper::of)
                .toList()
        );
    }

    public static NoExistProductRatingCommand of(final NoExistProductRatingRequest request) {
        return new NoExistProductRatingCommand(
            request.productType(),
            request.productName(),
            request.price(),
            request.rating()
        );
    }

    public static ProductRatingCommand of(final ProductRatingRequest request) {
        return new ProductRatingCommand(
            request.productId(),
            request.rating()
        );
    }

    public static PostResponse of(final PostDetailInfo post) {

        return new PostResponse(
            post.postId(),
            post.postTopic(),
            post.title(),
            new PostResponse.UserInfo(
                post.userId(),
                post.nickname(),
                post.profileImage(),
                post.reviewCount(),
                post.followerCount(),
                post.isFollowed()
            ),
            post.images(),
            post.content(),
            post.likeCount(),
            post.commentCount(),
            post.isUserLiked(),
            post.isUserCommented(),
            post.createdDate()
        );
    }

    public static PostUpdateCommand of(final PostRequest request, final Long postId) {
        return new PostUpdateCommand(
            postId,
            request.title(),
            request.content(),
            request.postTopic(),
            request.images()
        );
    }
}
