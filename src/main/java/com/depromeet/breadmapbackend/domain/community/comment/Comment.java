package com.depromeet.breadmapbackend.domain.community.comment;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.domain.post.comment.CommentStatus;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Comment
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    public static final String BLOCKED_USER_COMMENT = "차단된 사용자입니다.";
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "community_id")
    private Long communityId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommunityType communityType;

    @Column(nullable = false)
    private boolean isFirstDepth;

    @Column(nullable = false)
    private Long parentId;

    @Column(nullable = false)
    private Long targetCommentUserId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.ACTIVE;

    public Comment(
        final User user,
        final Long communityId,
        final String content,
        final boolean isFirstDepth,
        final Long parentId,
        final Long targetCommentUserId,
        final CommunityType communityType
    ) {
        this.user = user;
        this.content = content;
        this.communityId = communityId;
        this.isFirstDepth = isFirstDepth;
        this.parentId = parentId;
        this.targetCommentUserId = targetCommentUserId;
        this.communityType = communityType;
    }

    public void update(final String content) {
        this.content = content;
    }

    public void delete() {
        this.status = CommentStatus.DELETED;
    }

    public void restore() {
        this.status = CommentStatus.ACTIVE;
    }

    public void block() {
        this.status = CommentStatus.BLOCKED;
    }
}
