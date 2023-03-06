package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.common.converter.BooleanToYNConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage extends BaseEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isNew;

    @Builder
    public ReviewImage(Review review, Bakery bakery, ImageType imageType, String image) {
        this.review = review;
        this.bakery = bakery;
        this.imageType = imageType;
        this.image = image;
        this.isNew = true;
    }

    public void unNew() {
        this.isNew = false;
    }
}
