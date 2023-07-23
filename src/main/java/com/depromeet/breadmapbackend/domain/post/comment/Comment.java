package com.depromeet.breadmapbackend.domain.post.comment;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;

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

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "post_id", insertable = false, updatable = false)
	private Post post;

	@Column(name = "post_id", insertable = false, updatable = false)
	private Long postId;

	@Column(nullable = false)
	private boolean isFirstDepth;

	@Column(nullable = false)
	private Long parentId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CommentStatus status = CommentStatus.ACTIVE;

	public Comment(
		final User user,
		final Long postId,
		final String content,
		final boolean isFirstDepth,
		final Long parentId
	) {
		this.user = user;
		this.content = content;
		this.postId = postId;
		this.isFirstDepth = isFirstDepth;
		this.parentId = parentId;
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
