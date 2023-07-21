package com.depromeet.breadmapbackend.domain.post.report;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostReport extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User reporter;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "post_id")
	private Post post;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PostReportReason reason;

	private String content;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isBlock = Boolean.FALSE;

	public void changeBlock() {
		this.isBlock = !this.isBlock;
	}
}
