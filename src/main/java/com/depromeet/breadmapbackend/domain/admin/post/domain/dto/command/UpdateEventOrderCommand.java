package com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command;

/**
 * UpdateEventOrderCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record UpdateEventOrderCommand(
	int order,
	Long managerId
) {
}
