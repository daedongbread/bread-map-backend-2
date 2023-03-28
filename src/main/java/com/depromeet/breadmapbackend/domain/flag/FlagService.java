package com.depromeet.breadmapbackend.domain.flag;

import com.depromeet.breadmapbackend.domain.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagBakeryDto;

import java.util.List;

public interface FlagService {
    List<FlagDto> findFlags(Long userId);
    void addFlag(String username, FlagRequest request);
    void updateFlag(String username, Long flagId, FlagRequest request);
    void removeFlag(String username, Long flagId);
    FlagBakeryDto findBakeryByFlag(Long flagId);
    void addBakeryToFlag(String username, Long flagId, Long bakeryId);
    void removeBakeryToFlag(String username, Long flagId, Long bakeryId);
}
