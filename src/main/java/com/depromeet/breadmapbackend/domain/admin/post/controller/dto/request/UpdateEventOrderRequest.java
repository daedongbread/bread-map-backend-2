package com.depromeet.breadmapbackend.domain.admin.post.controller.dto.request;

import javax.validation.constraints.NotNull;

/**
 * UpdateEventOrderRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record UpdateEventOrderRequest(
	@NotNull int order,
	@NotNull Long managerId
) {
}
