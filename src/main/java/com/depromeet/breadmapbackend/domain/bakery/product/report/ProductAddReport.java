package com.depromeet.breadmapbackend.domain.bakery.product.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String price;

    @OneToMany(mappedBy = "productAddReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAddReportImage> images = new ArrayList<>();

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isNew = Boolean.TRUE;

    @Builder
    public ProductAddReport(Bakery bakery, User user, String name, String price) {
        this.bakery = bakery;
        this.user = user;
        this.name = name;
        this.price = price;
    }

    public void unNew() {
        this.isNew = false;
    }
}
