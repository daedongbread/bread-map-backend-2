package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.common.StringListConverter;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BreadReview extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bread_id")
    private Bread bread;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @Convert(converter = StringListConverter.class)
    private List<String> imageList = new ArrayList<>();

    @Builder
    private BreadReview(User user, Bakery bakery, Bread bread, String content, Integer rating, List<String> imageList) {
        this.user = user;
        this.bakery = bakery;
        this.bread = bread;
        this.content = content;
        this.rating = rating;
        this.imageList = imageList;
//        bakery.addBreadReview(this);
    }
}
