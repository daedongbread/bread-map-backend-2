package com.depromeet.breadmapbackend.domain.admin.curation.domain;

import org.springframework.stereotype.Component;

@Component
public class CurationEvent implements CurationContext {
	@Override
	public boolean support(String curationType) {
		return false;
	}

	@Override
	public void execute(Long curationId) {

	}
}
