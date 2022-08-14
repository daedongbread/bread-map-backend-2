package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BreadRating extends BaseEntity{
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "bakery_id")
//    private Bakery bakery;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "bread_id")
    private Bread bread;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(nullable = false)
    private Long rating;

    @Builder
    private BreadRating(/*User user, Bakery bakery, */Bread bread, Review review, Long rating) {
//        this.user = user;
//        this.bakery = bakery;
        this.bread = bread;
        this.review = review;
        this.rating = rating;
//        this.bread.getBreadRatingList().add(this);
    }
}
