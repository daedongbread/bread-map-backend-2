package com.depromeet.breadmapbackend.domain.admin.curation.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationBakery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "curation_id")
	private Curation curation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bakery_id")
	private Bakery bakery;

	public CurationBakery(Curation curation, Bakery bakery) {
		this.curation = curation;
		this.bakery = bakery;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CurationBakery currentBakery = (CurationBakery)o;
		return Objects.equals(curation, currentBakery.getCuration())
			&& Objects.equals(bakery, currentBakery.getBakery());
	}

	@Override
	public int hashCode() {
		return Objects.hash(curation, bakery);
	}
}
