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
public class Bread extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    private String image;

    @Builder
    private Bread(String name, Integer price, Bakery bakery, String image) {
        this.name = name;
        this.price = price;
        this.bakery = bakery;
        this.image = image;
//        bakery.getBreadList().add(this);
    }

    public void updateImgPath(String image) {
        this.image = image;
    }

}
