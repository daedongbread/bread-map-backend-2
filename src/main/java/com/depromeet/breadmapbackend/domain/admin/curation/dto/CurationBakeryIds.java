package com.depromeet.breadmapbackend.domain.admin.curation.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CurationBakeryIds {

	private List<Long> ids;

	public CurationBakeryIds(List<Long> ids) {
		this.ids = ids;
	}
}
