package com.depromeet.breadmapbackend.domain.product;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.common.converter.StringListConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductAddReport extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String price;

    @Convert(converter = StringListConverter.class)
    private List<String> images = new ArrayList<>();

    @Builder
    public ProductAddReport(Bakery bakery, String name, String price) {
        this.bakery = bakery;
        this.name = name;
        this.price = price;
    }

    public void addImage(String image) {
        this.images.add(image);
    }
}
