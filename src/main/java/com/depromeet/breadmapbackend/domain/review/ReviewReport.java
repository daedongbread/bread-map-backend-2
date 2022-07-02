package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewReport extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(nullable = false)
    private ReviewReportReason reason;

    private String content;

    @Builder
    public ReviewReport(Review review, ReviewReportReason reason, String content) {
        this.review = review;
        this.reason = reason;
        this.content = content;
    }
}
