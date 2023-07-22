package com.depromeet.breadmapbackend.domain.post.comment.like;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.depromeet.breadmapbackend.domain.post.comment.Comment;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike extends BaseEntity {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "comment_id")
	private Comment comment;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	public CommentLike(final Comment comment, final User user) {
		this.comment = comment;
		this.user = user;
	}
}
