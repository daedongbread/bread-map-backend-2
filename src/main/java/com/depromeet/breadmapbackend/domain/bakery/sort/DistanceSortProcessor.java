package com.depromeet.breadmapbackend.domain.bakery.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.BakerySortType;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryCardDto;

/**
 * DistanceSortProcessor
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/06/26
 */

@Component
public class DistanceSortProcessor implements SortProcessor {

	@Override
	public boolean supports(final BakerySortType bakerySortType) {
		return bakerySortType == BakerySortType.DISTANCE;
	}

	@Override
	public Comparator<BakeryCardDto> getComparator() {
		return Comparator.comparing(BakeryCardDto::getDistance);
	}
}
