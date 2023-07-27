package com.depromeet.breadmapbackend.domain.post.dto;

import java.time.LocalDateTime;

import com.depromeet.breadmapbackend.domain.post.PostTopic;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;

/**
 * CommunityCardInfo
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/21
 */
public record CommunityCardInfo(
	Long userId,
	String nickname,
	String profileImage,
	Long postId,
	String title,
	String content,
	String thumbnail,
	LocalDateTime createdDate,
	PostTopic topic,
	long likeCount,
	long commentCount,
	Long bakeryId,
	String name,
	String address,
	String bakeryThumbnail,
	boolean isUserLiked,
	boolean isUserCommented
) {
	public CommunityCardResponse toResponse() {

		final CommunityCardResponse.UserInfo userInfo =
			new CommunityCardResponse.UserInfo(
				this.userId,
				this.nickname,
				this.profileImage
			);
		final CommunityCardResponse.BakeryInfo bakeryInfo =
			new CommunityCardResponse.BakeryInfo(
				this.bakeryId,
				this.name,
				this.address,
				this.bakeryThumbnail
			);
		return new CommunityCardResponse(
			userInfo,
			this.postId,
			this.title,
			this.content,
			this.likeCount,
			this.commentCount,
			this.thumbnail,
			this.topic,
			this.createdDate,
			bakeryInfo,
			this.isUserLiked,
			this.isUserCommented
		);

	}
}
