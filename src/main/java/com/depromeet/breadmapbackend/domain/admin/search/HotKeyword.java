package com.depromeet.breadmapbackend.domain.admin.search;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SearchKeyword
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class HotKeyword extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String keyword;
	private int rank;

	public HotKeyword(final String keyword, final int rank) {
		this.keyword = keyword;
		this.rank = rank;
	}

	public static HotKeyword createSearchKeyword(final String keyword, final int rank) {
		return new HotKeyword(keyword, rank);
	}

	public void updateRank(final int rank) {
		this.rank = rank;
	}
}
