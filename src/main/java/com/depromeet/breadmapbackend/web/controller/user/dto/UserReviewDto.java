package com.depromeet.breadmapbackend.web.controller.user.dto;

import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.web.controller.review.dto.BreadRatingDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class UserReviewDto {
    private Long id;

    private String bakeryName;
    private String bakeryAddress;

    private List<BreadRatingDto> breadRatingDtoList;
    private List<String> imageList;
    private String content;

    private Integer likeNum;
    private Integer commentNum;
    private LocalDateTime createdAt;

    @Builder
    public UserReviewDto(Review review) {
        this.id = review.getId();
        this.bakeryName = review.getBakery().getName();
        this.bakeryAddress = review.getBakery().getAddress();
        this.breadRatingDtoList = review.getRatings().stream().map(BreadRatingDto::new).collect(Collectors.toList());
        this.imageList = review.getImageList().stream().map(ReviewImage::getImage).collect(Collectors.toList());
        this.content = review.getContent();
        this.likeNum = review.getLikes().size();
        this.commentNum = review.getComments().size();
        this.createdAt = review.getCreatedAt();
    }
}
