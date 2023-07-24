package com.depromeet.breadmapbackend.domain.post.dto;

import java.util.List;

import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.post.PostTopic;
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
	PostTopic postTopic
) {
	public Post toEntity(final User user) {
		return Post.builder()
			.title(this.title)
			.content(this.content)
			.postTopic(postTopic)
			.user(user)
			.build()
			.addImages(this.images);
	}
}
