package com.depromeet.breadmapbackend.domain.admin.curation.domain;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.admin.curation.service.CurationService;

@Component
public class LandingEvent implements CurationContext {

	private static final String EVENTNAME = "LANDING";
	private final CurationService curationService;

	public LandingEvent(CurationService curationService) {
		this.curationService = curationService;
	}

	@Override
	public boolean support(String curationType) {
		return EVENTNAME.equalsIgnoreCase(curationType);
	}

	@Override
	public void execute(Long curationId) {

	}
}
