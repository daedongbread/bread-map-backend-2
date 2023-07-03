package com.depromeet.breadmapbackend.domain.admin.curation.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Curation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private CurationBakeries bakeries;

	private String subTitle;

	private String description;

	private String thumbnailUrl;

	private String redirectUrl;

	private int like;

	private boolean isActive;

	@Enumerated(value = EnumType.STRING)
	private CurationType curationType;

	public void addAll(List<Bakery> bakeries) {
		this.bakeries.addAll(this, bakeries);
	}

	public void removeAllBakeries() {
		this.bakeries.clear();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Curation curation = (Curation)o;
		return Objects.equals(id, curation.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
