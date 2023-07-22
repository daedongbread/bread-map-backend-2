package com.depromeet.breadmapbackend.domain.post.dto;

import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.post.image.PostImage;

/**
 * PostDetailQuery
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/22
 */
public record PostDetailQuery(
	Post post,
	long likeCount,
	long commentCount,
	long reviewCount,
	long followerCount,
	boolean isFollowed
) {

	public PostDetailInfo toInfo() {
		return new PostDetailInfo(
			this.post().getId(),
			this.post().getPostTopic(),
			this.post().getTitle(),
			this.post().getImages().stream().map(PostImage::getImage).toList(),
			this.post().getContent(),
			this.post().getCreatedAt(),
			this.post().getUser().getId(),
			this.post().getUser().getNickName(),
			this.post().getUser().getUserInfo().getImage(),
			this.likeCount(),
			this.commentCount(),
			this.reviewCount(),
			this.followerCount(),
			this.isFollowed()
		);
	}
}
