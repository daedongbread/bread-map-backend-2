package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.BreadReview;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleReviewDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String content;
    private List<String> imageList;
    private Long bakery_id;
    private Long user_id;
    private List<Rating> ratings;
    private boolean isUse;

    public SimpleReviewDto(BreadReview breadReview) {
        this.id = breadReview.getId();
        this.createdAt = breadReview.getCreatedAt();
        this.modifiedAt = breadReview.getModifiedAt();
        this.content = breadReview.getContent();
        this.imageList = breadReview.getImageList();
        this.bakery_id = breadReview.getBakery().getId();
        this.user_id = breadReview.getUser().getId();
        this.ratings = Rating.RatingList(breadReview.getRatings());
        this.isUse = breadReview.isUse();
    }

    @Getter
    static class Rating{
        private Long rating_id;
        private Long rating;

        static List<Rating> RatingList(List<BreadRating> breadRatings){
            List<Rating> list = new ArrayList<>();
            breadRatings.forEach(rating -> {
                list.add(new Rating(rating));
            });
            return list;
        }

        public Rating(BreadRating breadRating){
            this.rating_id = breadRating.getId();
            this.rating = breadRating.getRating();
        }
    }
}
