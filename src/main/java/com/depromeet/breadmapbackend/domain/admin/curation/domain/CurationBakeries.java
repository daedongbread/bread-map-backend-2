package com.depromeet.breadmapbackend.domain.admin.curation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

@Embeddable
public class CurationBakeries {

	private static final int CURATION_MAXIMUM_BAKERY_SIZE = 5;

	@OneToMany(
		mappedBy = "bakeries",
		fetch = FetchType.LAZY,
		cascade = CascadeType.PERSIST,
		orphanRemoval = true
	)
	private List<CurationBakery> bakeries;

	public CurationBakeries() {
		this(new ArrayList<>());
	}

	public CurationBakeries(List<CurationBakery> bakeries) {
		this.bakeries = bakeries;
	}

	public List<CurationBakery> getBakeries() {
		return this.bakeries;
	}

	public void clear() {
		this.bakeries.clear();
	}

	public void addAll(Curation curation, List<Bakery> bakeries) {

		validateDuplicateBakery(bakeries);
		validatedMaximumSizeBakeries(bakeries);

		bakeries.stream()
			.map(bakery -> new CurationBakery(curation, bakery))
			.forEach(this.bakeries::add);
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

	public void add(Curation curation, Bakery bakery) {
		this.addAll(curation, List.of(bakery));
	}
}
