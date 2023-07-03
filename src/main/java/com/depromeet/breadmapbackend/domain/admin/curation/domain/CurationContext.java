package com.depromeet.breadmapbackend.domain.admin.curation.domain;

public interface CurationContext {

	boolean support(String curationType);

	void execute(Long curationId);
}
