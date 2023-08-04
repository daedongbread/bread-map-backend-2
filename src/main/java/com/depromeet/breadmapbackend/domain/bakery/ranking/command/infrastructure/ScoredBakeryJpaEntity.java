package com.depromeet.breadmapbackend.domain.bakery.ranking.command.infrastructure;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.ScoredBakery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ScoredBakery
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScoredBakeryJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	@JoinColumn(name = "bakery_id")
	private Bakery bakery;
	private long viewCount;
	private long flagCount;
	private double rating;
	private double totalScore;
	private LocalDate calculatedDate;
	@Column(name = "bakery_rank")
	private int rank;

	public ScoredBakery toDomain() {
		return new ScoredBakery(id, bakery, viewCount, flagCount, rating, totalScore, calculatedDate, rank);
	}
}
