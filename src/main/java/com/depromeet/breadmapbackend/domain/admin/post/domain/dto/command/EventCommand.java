package com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command;

import java.util.List;

import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.post.PostTopic;
import com.depromeet.breadmapbackend.domain.user.User;

/**
 * CreateEventCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public record EventCommand(
	boolean isPosted,
	boolean isFixed,
	boolean isCarousel,
	String title,
	String content,
	String bannerImage,
	List<String> images
) {
	public Post toEventPost(final User user) {
		return new Post(
			PostTopic.EVENT,
			user,
			this.title,
			this.content
		);
	}
}
