package com.depromeet.breadmapbackend.domain.bakery.product.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;
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
    private Boolean isNew = Boolean.FALSE;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isRegistered = Boolean.FALSE;

    @Builder
    public ProductAddReportImage(ProductAddReport productAddReport, String image) {
        this.productAddReport = productAddReport;
        this.bakery = productAddReport.getBakery();
        this.image = image;
        this.productAddReport.getImages().add(this);
    }

    public void unNew() {
        this.isNew = false;
    }

    public void register() {
        this.isRegistered = true;
        this.isNew = true;
    }
}

