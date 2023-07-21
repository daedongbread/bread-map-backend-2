package com.depromeet.breadmapbackend.domain.post.comment;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;

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
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false, length = 500)
	private String content;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isBlock = Boolean.FALSE;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isHide = Boolean.FALSE; // 관리자 신규 리뷰 숨김

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isDelete = Boolean.FALSE;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "post_id", insertable = false, updatable = false)
	private Post post;

	@Column(nullable = false)
	private long parentId;

}
