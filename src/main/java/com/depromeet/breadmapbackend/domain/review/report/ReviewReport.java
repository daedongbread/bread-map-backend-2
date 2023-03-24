package com.depromeet.breadmapbackend.domain.review.report;

import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
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
public class ReviewReport extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(nullable = false)
    private ReviewReportReason reason;

    private String content;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isBlock; // TODO

    @Builder
    public ReviewReport(User reporter, Review review, ReviewReportReason reason, String content) {
        this.reporter = reporter;
        this.review = review;
        this.reason = reason;
        this.content = content;
        this.isBlock = false;
    }

    public void changeBlock() {
        this.isBlock = !this.isBlock;
    }
}
