package com.depromeet.breadmapbackend.domain.admin.feed.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

@Embeddable
public class CurationBakeries {

	private static final int CURATION_MAXIMUM_BAKERY_SIZE = 5;

	@OneToMany(
		mappedBy = "curation",
		fetch = FetchType.LAZY,
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
	private List<CurationBakery> bakeries;

	public CurationBakeries() {
		this(new ArrayList<>());
	}

	public CurationBakeries(List<CurationBakery> bakeries) {
		this.bakeries = bakeries;
	}

	public List<CurationBakery> getCurationBakeries() {
		return this.bakeries;
	}

	public List<Long> getProductIdList() {
		return bakeries.stream().map(CurationBakery::getProductId).collect(Collectors.toList());
	}

	public List<Bakery> getBakeries() {
		return bakeries.stream().map(CurationBakery::getBakery).collect(Collectors.toList());
	}

	public void clear() {
		this.bakeries.clear();
	}

	public void addAll(List<Bakery> bakeries, List<CurationBakery> curationBakeries) {

		validateDuplicateBakery(bakeries);
		validatedMaximumSizeBakeries(bakeries);
		validatedBakeryStatus(bakeries);

		this.bakeries.addAll(curationBakeries);
	}

	private void validatedBakeryStatus(List<Bakery> bakeries) {
		for (Bakery bakery : bakeries) {
			if (!bakery.isPosting()) {
				throw new DaedongException(DaedongStatus.BAKERY_NOT_POSTING);
			}
		}
	}

	private void validateDuplicateBakery(List<Bakery> bakeries) {
		long distinctCountOfCurationBakeries = bakeries.stream()
			.map(Bakery::getId)
			.distinct()
			.count();

		if (distinctCountOfCurationBakeries != bakeries.size()) {
			throw new DaedongException(DaedongStatus.CURATION_DUPLICATE_EXCEPTION);
		}
	}

	private void validatedMaximumSizeBakeries(List<Bakery> bakeries) {
		if (bakeries.size() > CURATION_MAXIMUM_BAKERY_SIZE) {
			throw new DaedongException(DaedongStatus.CURATION_BAKERY_SIZE_EXCEPTION);
		}
	}
}
