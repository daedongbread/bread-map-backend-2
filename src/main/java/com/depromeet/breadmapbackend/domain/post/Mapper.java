package com.depromeet.breadmapbackend.domain.post;

import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.post.dto.request.PostRequest;
import com.depromeet.breadmapbackend.domain.post.dto.response.PostResponse;
import com.depromeet.breadmapbackend.domain.post.image.PostImage;
import com.depromeet.breadmapbackend.domain.user.User;

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

	public static PostResponse of(final PostDetailInfo post) {
		final User postUser = post.post().getUser();
		final Post selectedPost = post.post();

		return new PostResponse(
			selectedPost.getId(),
			selectedPost.getPostTopic(),
			selectedPost.getTitle(),
			new PostResponse.UserInfo(
				postUser.getId(),
				postUser.getNickName(),
				postUser.getUserInfo().getImage(),
				post.reviewCount(),
				post.followerCount(),
				post.isFollowed()
			),
			selectedPost.getImages().stream().map(PostImage::getImage).toList(),
			selectedPost.getContent(),
			post.likeCount(),
			post.commentCount(),
			selectedPost.getCreatedAt()
		);
	}
}
