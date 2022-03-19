package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bread_category_id")
    private BreadCategory breadCategory;

    private String imgPath;

    @Builder
    private Menu(String name, Integer price, Bakery bakery, BreadCategory breadCategory, String imgPath) {
        this.name = name;
        this.price = price;
        this.bakery = bakery;
        this.breadCategory = breadCategory;
        this.imgPath = imgPath;
    }

    public void updateImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

}
