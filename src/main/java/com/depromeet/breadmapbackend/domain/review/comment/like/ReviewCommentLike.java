package com.depromeet.breadmapbackend.domain.review.comment.like;

import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewComment;
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
public class ReviewCommentLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "reviewComment_id")
    private ReviewComment reviewComment;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public ReviewCommentLike(ReviewComment reviewComment, User user) {
        this.reviewComment = reviewComment;
        this.user = user;
        this.reviewComment.getLikes().add(this);
    }
}
