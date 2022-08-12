package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Optional;

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

    @Column(nullable = false/*, columnDefinition = "boolean default 1"*/)
    @ColumnDefault("true")
    private boolean isTrue;

    @Builder
    private Bread(String name, Integer price, Bakery bakery, String image, Boolean isTrue) {
        this.name = name;
        this.price = price;
        this.bakery = bakery;
        this.image = image;
        if(isTrue == null) this.isTrue = true;
        else this.isTrue = isTrue;
//        bakery.getBreadList().add(this);
    }

    public void updateName(String name) {
        if(!this.name.equals(name)) this.name = name;
    }

    public void updatePrice(Integer price) {
        if(!this.price.equals(price)) this.price = price;
    }

    public void updateImage(String image) {
        this.image = image;
    }
}
