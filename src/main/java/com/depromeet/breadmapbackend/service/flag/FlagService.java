package com.depromeet.breadmapbackend.service.flag;

import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagBakeryDto;

import java.util.List;

public interface FlagService {
    List<FlagDto> findFlags(String username);
    void addFlag(String username, FlagRequest request);
    void updateFlag(String username, Long flagId, FlagRequest request);
    void removeFlag(String username, Long flagId);
    List<FlagBakeryDto> findBakeryByFlag(String username, Long flagId);
    void addBakeryToFlag(String username, Long flagId, Long bakeryId);
    void removeBakeryToFlag(String username, Long flagId, Long bakeryId);
}
