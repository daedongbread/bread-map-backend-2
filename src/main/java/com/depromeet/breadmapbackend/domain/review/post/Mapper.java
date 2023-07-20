package com.depromeet.breadmapbackend.domain.review.post;

import com.depromeet.breadmapbackend.domain.review.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.review.post.dto.request.PostRequest;

/**
 * Mapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
public class Mapper {

	public static PostRegisterCommand of(final PostRequest request) {
		return new PostRegisterCommand(
			request.title(),
			request.content(),
			request.images(),
			PostTopic.BREAD_STORY
		);
	}
}
