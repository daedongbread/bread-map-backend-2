package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Menu;
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
public class MenuReview extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(nullable = false, length = 200)
    private String contents;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> imgPathList = new ArrayList<>();

    @Builder
    private MenuReview(User member, Bakery bakery, Menu menu, String contents, Integer rating, List<String> imgPathList) {
        this.member = member;
        this.bakery = bakery;
        this.menu = menu;
        this.contents = contents;
        this.rating = rating;
        this.imgPathList = imgPathList;
    }

}
