package com.depromeet.breadmapbackend.domain.review.view;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewView extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Review review;

    @Column(nullable = false)
    private Integer viewNum;


    @Builder
    public ReviewView(Review review) {
        this.review = review;
        this.viewNum = 0;
    }

    public void viewReview() {
        this.viewNum += 1;
    }
}
