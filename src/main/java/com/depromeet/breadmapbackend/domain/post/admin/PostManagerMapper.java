package com.depromeet.breadmapbackend.domain.post.admin;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
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
	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "post_id")
	private Post postId;
	private boolean isFixed;
	private boolean isProcessed;
	private boolean isCarousal;
}
