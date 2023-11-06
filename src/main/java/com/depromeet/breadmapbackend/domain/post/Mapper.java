package com.depromeet.breadmapbackend.domain.post;

import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.post.dto.PostUpdateCommand;
import com.depromeet.breadmapbackend.domain.post.dto.request.PostRequest;
import com.depromeet.breadmapbackend.domain.post.dto.response.PostResponse;

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
			request.postTopic()
		);
	}

	public static PostResponse of(final PostDetailInfo post) {

		return new PostResponse(
			post.postId(),
			post.postTopic(),
			post.title(),
			new PostResponse.UserInfo(
				post.userId(),
				post.nickname(),
				post.profileImage(),
				post.reviewCount(),
				post.followerCount(),
				post.isFollowed()
			),
			post.images(),
			post.content(),
			post.likeCount(),
			post.commentCount(),
			post.isUserLiked(),
			post.isUserCommented(),
			post.createdDate()
		);
	}

	public static PostUpdateCommand of(final PostRequest request, final Long postId) {
		return new PostUpdateCommand(
			postId,
			request.title(),
			request.content(),
			request.postTopic(),
			request.images()
		);
	}
}
