package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import lombok.*;

import java.time.LocalDateTime;
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

    private List<BreadRatingDto> breadRatingDtoList;
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
        this.userImage = review.getUser().getProfileImageUrl();
        this.nickName = review.getUser().getNickName();
        this.reviewNum = reviewNum; //TODO : User에 List<Review>?
        this.followerNum = followerNum;
        this.breadRatingDtoList = review.getRatings().stream().map(BreadRatingDto::new).collect(Collectors.toList());
        this.imageList = review.getImageList();
        this.content = review.getContent();
        this.likeNum = review.getLikes().size();
        this.commentNum = (int) review.getComments().stream().filter(reviewComment -> reviewComment.getUser() != null).count();
        this.createdAt = review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.averageRating = Math.floor(
                review.getRatings().stream().map(BreadRating::getRating).mapToLong(Long::longValue)
                        .average().orElse(0)*10)/10.0;
    }
}
