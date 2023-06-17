package com.depromeet.breadmapbackend.domain.flag;

import java.util.List;

import com.depromeet.breadmapbackend.domain.flag.dto.FlagBakeryDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagRequest;

public interface FlagService {
	List<FlagDto> getFlags(Long userId);

	void addFlag(Long userId, FlagRequest request);

	void updateFlag(Long userId, Long flagId, FlagRequest request);

	void removeFlag(Long userId, Long flagId);

	FlagBakeryDto getBakeriesByFlag(Long userId, Long flagId);

	void addBakeryToFlag(Long userId, Long flagId, Long bakeryId);

	void removeBakeryToFlag(Long userId, Long flagId, Long bakeryId);
}
