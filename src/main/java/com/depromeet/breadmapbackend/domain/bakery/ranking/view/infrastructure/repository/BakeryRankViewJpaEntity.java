package com.depromeet.breadmapbackend.domain.bakery.ranking.view.infrastructure.repository;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * QueryBakeryRankJpaEntity
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BakeryRankViewJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int rank;
	private Long bakeryId;
	private LocalDate calculatedDate;
	private String name;
	private String image;
	private double rating;
	private long flagCount;
	private String shortAddress;
	private Integer version;

	public static BakeryRankViewJpaEntity fromDomain(BakeryRankView bakeryRankView) {
		return BakeryRankViewJpaEntity.builder()
			.id(bakeryRankView.id())
			.rank(bakeryRankView.rank())
			.bakeryId(bakeryRankView.bakeryId())
			.calculatedDate(bakeryRankView.calculatedDate())
			.name(bakeryRankView.name())
			.image(bakeryRankView.image())
			.rating(bakeryRankView.rating())
			.flagCount(bakeryRankView.flagCount())
			.shortAddress(bakeryRankView.shortAddress())
			.version(bakeryRankView.version())
			.build();
	}

	public BakeryRankView toDomain() {
		return new BakeryRankView(
			this.id,
			this.rank,
			this.bakeryId,
			this.calculatedDate,
			this.name,
			this.image,
			this.rating,
			this.flagCount,
			this.shortAddress,
			this.version
		);
	}
}
