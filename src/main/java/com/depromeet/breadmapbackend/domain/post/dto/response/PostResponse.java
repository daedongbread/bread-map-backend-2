package com.depromeet.breadmapbackend.domain.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.depromeet.breadmapbackend.domain.post.PostTopic;

public record PostResponse(
	Long postId,
	PostTopic postTopic,
	String title,
	UserInfo writerInfo,
	List<String> images,
	String content,
	long likeCount,
	long commentCount,
	boolean isUserLiked,
	boolean isUserCommented,
	LocalDateTime createdDate

) {
	public record UserInfo(
		Long userId,
		String nickname,
		String profileImage,
		long reviewCount,
		long followerCount,
		boolean isFollowed
	) {
	}
}
