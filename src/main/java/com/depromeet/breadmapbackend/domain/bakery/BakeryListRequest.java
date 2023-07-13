package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

public record BakeryListRequest(

	@EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
	BakerySortType sortBy,
	Boolean filterBy,
	Double latitude,
	Double longitude,
	Double latitudeDelta,
	Double longitudeDelta
) {
}