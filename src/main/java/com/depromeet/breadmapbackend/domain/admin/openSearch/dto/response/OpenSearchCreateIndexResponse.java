package com.depromeet.breadmapbackend.domain.admin.openSearch.dto.response;

import lombok.Getter;

@Getter
public class OpenSearchCreateIndexResponse {

	private String message;

	public OpenSearchCreateIndexResponse(String message) {
		this.message = message;
	}
}
