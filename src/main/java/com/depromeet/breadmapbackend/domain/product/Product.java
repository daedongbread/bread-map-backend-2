package com.depromeet.breadmapbackend.domain.product;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String price;

    private String image;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewProductRating> reviewProductRatingList = new ArrayList<>();

    @Column(nullable = false/*, columnDefinition = "boolean default 1"*/)
    @ColumnDefault("true")
    private boolean isTrue;

    @Builder
    private Product(ProductType productType, String name, String price, Bakery bakery, Boolean isTrue) {
        this.productType = productType;
        this.name = name;
        this.price = price;
        this.bakery = bakery;
        if(isTrue == null) this.isTrue = true;
        else this.isTrue = isTrue;
//        this.bakery.getBreadList().add(this);
//        bakery.getBreadList().add(this);
    }

    public void updateType(ProductType productType) { this.productType = productType; }

    public void updateName(String name) {
        if(!this.name.equals(name)) this.name = name;
    }

    public void updatePrice(String price) {
        if(!this.price.equals(price)) this.price = price;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void update(String name, String price) {
        this.name = name;
        this.price = price;
    }
}
