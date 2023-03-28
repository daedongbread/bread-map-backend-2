package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BakeryUpdateReportImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakeryUpdateReport_id")
    private BakeryUpdateReport bakeryUpdateReport;

    @Column(nullable = false)
    private String image;

    @Builder
    public BakeryUpdateReportImage(Bakery bakery, BakeryUpdateReport report, String image) {
        this.bakery = bakery;
        this.bakeryUpdateReport = report;
        this.image = image;
        this.bakeryUpdateReport.getImages().add(this);
    }
}
