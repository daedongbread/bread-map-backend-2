package com.depromeet.breadmapbackend.domain.post.image;

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
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends BaseEntity {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "post_id")
	private Post post;

	@Column(nullable = false)
	private String image;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isRegistered = Boolean.FALSE;

	@Builder
	public PostImage(final Post post, final String image) {
		this.post = post;
		this.image = image;
	}

	public void register() {
		this.isRegistered = true;
	}

	public void unregister() {
		this.isRegistered = false;
	}
}
