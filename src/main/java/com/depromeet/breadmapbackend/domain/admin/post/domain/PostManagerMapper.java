package com.depromeet.breadmapbackend.domain.admin.post.domain;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PostManagerMapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/21
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostManagerMapper extends BaseEntity {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	@OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "post_id")
	private Post post;
	private boolean isFixed;
	private boolean isPosted;

	@Builder
	public PostManagerMapper(
		final Post post,
		final boolean isFixed,
		final boolean isPosted
	) {
		this.post = post;
		this.isFixed = isFixed;
		this.isPosted = isPosted;
	}

	public void unFix() {
		this.isFixed = false;
	}

	public void update(
		final boolean isFixed,
		final boolean isPosted
	) {
		this.isFixed = isFixed;
		this.isPosted = isPosted;
	}

}
