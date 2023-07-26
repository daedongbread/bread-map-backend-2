package com.depromeet.breadmapbackend.domain.post;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.depromeet.breadmapbackend.domain.post.image.PostImage;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Commutity
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PostTopic postTopic;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 500)
	private String content;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostImage> images = new ArrayList<>();

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isBlock = Boolean.FALSE;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isHide = Boolean.FALSE;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isDelete = Boolean.FALSE;

	@Builder
	public Post(
		final PostTopic postTopic,
		final User user,
		final String title,
		final String content
	) {
		this.postTopic = postTopic;
		this.user = user;
		this.title = title;
		this.content = content;
	}

	public Post addImages(final List<String> imageList) {
		imageList.stream()
			.map(image -> PostImage.builder()
				.post(this)
				.image(image)
				.build()
			).forEach(this.images::add);
		return this;
	}

	public void update(final String content, final String title, final List<String> images) {
		this.content = content;
		this.title = title;
		this.images.clear();
		this.addImages(images);
	}
}
