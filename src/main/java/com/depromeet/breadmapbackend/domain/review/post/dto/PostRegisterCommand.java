package com.depromeet.breadmapbackend.domain.review.post.dto;

import java.util.List;

import com.depromeet.breadmapbackend.domain.review.post.Post;
import com.depromeet.breadmapbackend.domain.review.post.PostTopic;
import com.depromeet.breadmapbackend.domain.user.User;

/**
 * PostRegisterDto
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
public record PostRegisterCommand(
	String title,
	String content,
	List<String> images,
	PostTopic topic
) {
	public Post toEntity(final User user) {
		return Post.builder()
			.title(title)
			.content(content)
			.postTopic(topic)
			.user(user)
			.build()
			.addImages(images);
	}
}
