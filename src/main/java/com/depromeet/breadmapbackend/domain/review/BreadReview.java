package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
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

    @Column(nullable = false, length = 200)
    private String content;

    @Convert(converter = StringListConverter.class)
    private List<String> imageList = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "boolean default 1")
    private boolean isUse;

    @OneToMany(mappedBy = "review_id")
    private List<BreadRating> ratings = new ArrayList<>();

    @Builder
    private BreadReview(User user, Bakery bakery, String content, List<String> imageList, boolean isUse) {
        this.user = user;
        this.bakery = bakery;
        this.content = content;
        this.imageList = imageList;
        this.isUse = isUse;
    }

    public void addRating(BreadRating breadRating){
        this.ratings.add(breadRating);
    }
}
