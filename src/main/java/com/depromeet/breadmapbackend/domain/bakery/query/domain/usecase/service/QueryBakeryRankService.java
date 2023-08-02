package com.depromeet.breadmapbackend.domain.bakery.query.domain.usecase.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryRank;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.repository.QueryBakeryRankRepository;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.usecase.QueryBakeryRankUseCase;

import lombok.RequiredArgsConstructor;

/**
 * QueryBakeryRankService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
@Service
@RequiredArgsConstructor
public class QueryBakeryRankService implements QueryBakeryRankUseCase {

	private final QueryBakeryRankRepository repository;

	@Override
	public List<Query> query(final int size) {
		final List<QueryBakeryRank> bakeryRanks = repository.findByCalculatedDate(LocalDate.now(),
			Pageable.ofSize(size));
		return null;
	}
}
