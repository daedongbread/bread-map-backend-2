package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private ReviewComment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewComment> childList = new ArrayList<>();

    @OneToMany(mappedBy = "reviewComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewCommentLike> likes = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "boolean default 0")
    private boolean isDelete;

    @Builder
    public ReviewComment(Review review, User user, String content, ReviewComment parent) {
        this.review = review;
        this.user = user;
        this.content = content;
        this.parent = parent;
        this.isDelete = false;
    }

    public void plusLike(ReviewCommentLike reviewCommentLike){ this.likes.add(reviewCommentLike); }

    public void minusLike(ReviewCommentLike reviewCommentLike){
        this.likes.remove(reviewCommentLike);
    }

    public void addChildComment(ReviewComment reviewComment) { this.childList.add(reviewComment); }

    public void removeChildComment(ReviewComment reviewComment) { this.childList.remove(reviewComment); }

    public void delete() {
        this.isDelete = true;
//        this.user = null;
//        this.content = "DELETE COMMENT";
    }
}
