package com.depromeet.breadmapbackend.web.controller.review.dto;

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
    private Long id;

    private Long userId;
    private String userImage;
    private String nickName;
    private Integer reviewNum;
    private Integer followerNum;

    private List<ProductRatingDto> productRatingList;
    private List<String> imageList;
    private String content;

    private Integer likeNum;
    private Integer commentNum;
    private String createdAt;

    private Double averageRating;

    @Builder
    public ReviewDto(Review review, Integer reviewNum, Integer followerNum) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.userImage = review.getUser().getImage();
        this.nickName = review.getUser().getNickName();
        this.reviewNum = reviewNum; //TODO : User에 List<Review>?
        this.followerNum = followerNum;
        this.productRatingList = review.getRatings().stream().map(ProductRatingDto::new).collect(Collectors.toList());
        this.imageList = review.getImageList().stream().map(ReviewImage::getImage).collect(Collectors.toList());
        this.content = review.getContent();
        this.likeNum = review.getLikes().size();
        this.commentNum = (int) review.getComments().stream().filter(reviewComment -> reviewComment.getUser() != null).count();
        this.createdAt = review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.averageRating = Math.floor(
                review.getRatings().stream().map(ReviewProductRating::getRating).mapToLong(Long::longValue)
                        .average().orElse(0)*10)/10.0;
    }
}
