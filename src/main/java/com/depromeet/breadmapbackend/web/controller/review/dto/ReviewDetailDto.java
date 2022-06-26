package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewDetailDto {
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
    private LocalDateTime createdAt;

    private List<ReviewCommentDto> comments;

    @Builder
    public ReviewDetailDto(Review review, Integer reviewNum, Integer followerNum) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.userImage = review.getUser().getProfileImageUrl();
        this.nickName = review.getUser().getNickName();
        this.reviewNum = reviewNum;
        this.followerNum = followerNum;
        this.breadRatingDtoList = review.getRatings().stream().map(BreadRatingDto::new).collect(Collectors.toList());
        this.imageList = review.getImageList();
        this.content = review.getContent();
        this.likeNum = review.getLikes().size();
        this.commentNum = (int) review.getComments().stream().filter(reviewComment -> reviewComment.getUser() != null).count();
        this.createdAt = review.getCreatedAt();
        this.comments = review.getComments().stream()
                .filter(reviewComment -> reviewComment.getParent() == null).map(ReviewCommentDto::new).collect(Collectors.toList());
    }
}
