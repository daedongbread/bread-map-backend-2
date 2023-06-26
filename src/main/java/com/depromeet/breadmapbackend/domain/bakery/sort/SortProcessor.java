package com.depromeet.breadmapbackend.domain.bakery.sort;

import java.util.Comparator;

import com.depromeet.breadmapbackend.domain.bakery.BakerySortType;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryCardDto;

/**
 * SortProcessor
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/06/26
 */
public interface SortProcessor {

	boolean supports(BakerySortType bakerySortType);

	Comparator<BakeryCardDto> getComparator();

}
