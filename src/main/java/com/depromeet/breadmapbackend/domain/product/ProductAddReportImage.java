package com.depromeet.breadmapbackend.domain.product;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.common.converter.BooleanToYNConverter;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductAddReportImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productAddReport_id")
    private ProductAddReport productAddReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isMain;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isNew;

    @Builder
    public ProductAddReportImage(ProductAddReport productAddReport, String image) {
        this.productAddReport = productAddReport;
        this.bakery = productAddReport.getBakery();
        this.image = image;
        this.isMain = this.productAddReport.getImages().isEmpty();
        this.isNew = true;
        this.productAddReport.getImages().add(this);
    }

    public void unNew() {
        this.isNew = false;
    }

    public void unMain() {
        this.isMain = false;
    }

    public void isMain() {
        this.isMain = true;
    }
}

