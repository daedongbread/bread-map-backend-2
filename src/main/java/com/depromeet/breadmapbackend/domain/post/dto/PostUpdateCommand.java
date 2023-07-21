package com.depromeet.breadmapbackend.domain.post.dto;

import java.util.List;

/**
 * PostUpdateCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/22
 */
public record PostUpdateCommand(
	Long postId,
	String title,
	String content,
	List<String> images
) {

}
