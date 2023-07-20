package com.depromeet.breadmapbackend.domain.review.post.dto.response;

import java.time.LocalDate;

import com.depromeet.breadmapbackend.domain.review.post.PostTopic;

/**
 * PostCardResponseDto
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
public record CommunityCardResponse(
	UserInfo writerInfo,
	Long postId,
	String title,
	String content,
	long likeCount,
	long commentCount,
	String thumbnail,
	PostTopic topic,
	LocalDate createdDate,
	BakeryInfo bakeryInfo
) {
	public record UserInfo(
		Long userId,
		String nickname,
		String profileImage
	) {
	}

	public record BakeryInfo(
		Long bakeryId,
		String name,
		String address,
		String thumbnail
	) {
	}
}
