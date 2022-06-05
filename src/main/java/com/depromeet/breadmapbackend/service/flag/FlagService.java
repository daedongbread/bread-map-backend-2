package com.depromeet.breadmapbackend.service.flag;

import com.depromeet.breadmapbackend.web.controller.flag.dto.AddFlagRequest;

public interface FlagService {
    void addFlag(String username, AddFlagRequest request);
    void removeFlag(String username, Long flagId);
    void addBakeryToFlag(String username, Long flagId, Long bakeryId);
    void removeBakeryToFlag(String username, Long flagId, Long bakeryId);
}
