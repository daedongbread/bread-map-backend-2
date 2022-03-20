package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.common.StringListConverter;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BakeryReview extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @Column(nullable = false, length = 200)
    private String contents;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> imgPathList = new ArrayList<>();

    @Builder
    private BakeryReview(User member, Bakery bakery, String contents, Integer rating, List<String> imgPathList) {
        this.member = member;
        this.bakery = bakery;
        this.contents = contents;
        this.rating = rating;
        this.imgPathList = imgPathList;
    }

    public void updateRating(Integer rating) {
        this.rating = rating;
    }

}
