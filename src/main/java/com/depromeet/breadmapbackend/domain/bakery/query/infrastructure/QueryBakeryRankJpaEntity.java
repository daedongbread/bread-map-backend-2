package com.depromeet.breadmapbackend.domain.bakery.query.infrastructure;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryRank;

import lombok.AccessLevel;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class QueryBakeryRankJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long bakeryId;
	private LocalDate calculatedDate;
	private String name;
	private String image;
	private double rating;
	private String shortAddress;

	public QueryBakeryRank toDomain() {
		return new QueryBakeryRank(
			bakeryId,
			calculatedDate,
			name,
			image,
			rating,
			shortAddress
		);
	}

}
