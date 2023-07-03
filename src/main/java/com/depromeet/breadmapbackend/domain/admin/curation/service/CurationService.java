package com.depromeet.breadmapbackend.domain.admin.curation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.curation.domain.Curation;
import com.depromeet.breadmapbackend.domain.admin.curation.repository.CurationRepository;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CurationService {

	private final CurationRepository curationRepository;
	private final BakeryRepository bakeryRepository;

	public Curation getCurationFeed(Long curationId) {

	}

	@Transactional
	public void updateExhibits(Long curationId, List<Long> curationBakeryIds) {

		Curation curation = findCurationById(curationId);
		List<Bakery> bakeries = bakeryRepository.findBakeriesInIds(curationBakeryIds);

		curation.removeAllBakeries();
		curation.addAll(bakeries);
	}

	@Transactional
	public void removeCuration(Long curationId) {
		curationRepository.deleteById(curationId);
	}

	private Curation findCurationById(Long curationId) {
		return curationRepository.findById(curationId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.CURATION_NOT_FOUND));
	}

	public void getAll() {
		curationRepository.findAllByActiveIsTrue();
	}
}
