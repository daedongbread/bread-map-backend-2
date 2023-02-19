package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewDto {
    private BakeryInfo bakeryInfo;
    private UserInfo userInfo;
    private ReviewInfo reviewInfo;

    @Getter
    @NoArgsConstructor
    public static class BakeryInfo {
        private Long bakeryId;
        private String bakeryImage;
        private String bakeryName;
        private String bakeryAddress;

        @Builder
        public BakeryInfo(Bakery bakery) {
            this.bakeryId = bakery.getId();
            this.bakeryImage = bakery.getImage();
            this.bakeryName = bakery.getName();
            this.bakeryAddress = bakery.getAddress();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String userImage;
        private String nickName;
        private Integer reviewNum;
        private Integer followerNum;
        private Boolean isFollow;
        private Boolean isMe;

        @Builder
        public UserInfo(Review review, Integer reviewNum, Integer followerNum, Boolean isFollow, Boolean isMe) {
            this.userId = review.getUser().getId();
            this.userImage = review.getUser().getImage();
            this.nickName = review.getUser().getNickName();
            this.reviewNum = reviewNum;
            this.followerNum = followerNum;
            this.isFollow = isFollow;
            this.isMe = isMe;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ReviewInfo {
        private Long id;
        private List<ProductRatingDto> productRatingList;
        private List<String> imageList;
        private String content;

        private Boolean isLike;
        private Integer likeNum;
        private Integer commentNum;
        private String createdAt;

        private Double averageRating;

        @Builder
        public ReviewInfo(Review review, Boolean isLike) {
            this.id = review.getId();
            this.productRatingList = review.getRatings().stream().map(ProductRatingDto::new).collect(Collectors.toList());
            this.imageList = review.getImageList().stream().map(ReviewImage::getImage).collect(Collectors.toList());
            this.content = review.getContent();
            this.isLike = isLike;
            this.likeNum = review.getLikes().size();
            this.commentNum = (int) review.getComments().stream().filter(reviewComment -> reviewComment.getUser() != null).count();
            this.createdAt = review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            this.averageRating = Math.floor(
                    review.getRatings().stream().map(ReviewProductRating::getRating).mapToLong(Long::longValue)
                            .average().orElse(0)*10)/10.0;
        }
    }

    @Builder
    public ReviewDto(Review review, Integer reviewNum, Integer followerNum, Boolean isFollow, Boolean isMe, Boolean isLike) {
        this.bakeryInfo = BakeryInfo.builder().bakery(review.getBakery()).build();
        this.userInfo = UserInfo.builder().review(review).reviewNum(reviewNum).followerNum(followerNum).isFollow(isFollow).isMe(isMe).build();
        this.reviewInfo = ReviewInfo.builder().review(review).isLike(isLike).build();
    }
}
