package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BakeryAddReportImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakeryAddReport_id")
    private BakeryAddReport bakeryAddReport;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isNew = Boolean.TRUE;

    @Builder
    public BakeryAddReportImage(BakeryAddReport report, String image) {
        this.bakeryAddReport = report;
        this.image = image;
        this.bakeryAddReport.getImages().add(this);
    }

    public void unNew() {
        this.isNew = false;
    }
}
