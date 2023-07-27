package com.depromeet.breadmapbackend.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.depromeet.breadmapbackend.domain.post.PostTopic;

public record PostDetailInfo(
	Long postId,
	PostTopic postTopic,
	String title,
	List<String> images,
	String content,
	LocalDateTime createdDate,
	Long userId,
	String nickname,
	String profileImage,
	long likeCount,
	long commentCount,
	long reviewCount,
	long followerCount,
	boolean isUserLiked,
	boolean isUserCommented,
	boolean isFollowed
) {
}
