package com.depromeet.breadmapbackend.domain.post.dto;

import com.depromeet.breadmapbackend.domain.post.Post;

public record PostDetailInfo(
	Post post,
	long likeCount,
	long commentCount,
	long reviewCount,
	long followerCount,
	boolean isFollowed
) {
}
