package com.depromeet.breadmapbackend.domain.flag;

import com.depromeet.breadmapbackend.domain.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagBakeryDto;

import java.util.List;

public interface FlagService {
    List<FlagDto> getFlags(Long userId);
    void addFlag(String oAuthId, FlagRequest request);
    void updateFlag(String oAuthId, Long flagId, FlagRequest request);
    void removeFlag(String oAuthId, Long flagId);
    FlagBakeryDto getBakeryByFlag(String oAuthId, Long flagId);
    void addBakeryToFlag(String oAuthId, Long flagId, Long bakeryId);
    void removeBakeryToFlag(String oAuthId, Long flagId, Long bakeryId);
}
