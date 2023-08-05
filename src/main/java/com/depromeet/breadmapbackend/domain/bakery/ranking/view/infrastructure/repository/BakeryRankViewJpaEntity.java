package com.depromeet.breadmapbackend.domain.bakery.ranking.view.infrastructure.repository;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

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
@Table(name = "bakery_rank_view")
@Entity
public class BakeryRankViewJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int bakeryRank;
	private Long bakeryId;
	private LocalDate calculatedDate;
	private String name;
	private String image;
	private double bakeryRating;
	private long flagCount;
	private String shortAddress;
	@Version
	private Integer version;

	public static BakeryRankViewJpaEntity fromDomain(BakeryRankView bakeryRankView) {
		return BakeryRankViewJpaEntity.builder()
			.id(bakeryRankView.id())
			.bakeryRank(bakeryRankView.rank())
			.bakeryId(bakeryRankView.bakeryId())
			.calculatedDate(bakeryRankView.calculatedDate())
			.name(bakeryRankView.name())
			.image(bakeryRankView.image())
			.bakeryRating(bakeryRankView.rating())
			.flagCount(bakeryRankView.flagCount())
			.shortAddress(bakeryRankView.shortAddress())
			.version(bakeryRankView.version())
			.build();
	}

	public BakeryRankView toDomain() {
		return new BakeryRankView(
			this.id,
			this.bakeryRank,
			this.bakeryId,
			this.calculatedDate,
			this.name,
			this.image,
			this.bakeryRating,
			this.flagCount,
			this.shortAddress,
			this.version
		);
	}
}
