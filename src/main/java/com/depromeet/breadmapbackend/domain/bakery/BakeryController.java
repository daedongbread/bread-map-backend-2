package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.bakery.dto.*;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/bakeries")
@RequiredArgsConstructor
public class BakeryController {
    private final BakeryService bakeryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<BakeryCardDto>> getBakeryList(
            @CurrentUser String username,
            @RequestParam BakerySortType sortBy, @RequestParam boolean filterBy,
            @RequestParam Double latitude, @RequestParam Double longitude,
            @RequestParam Double latitudeDelta, @RequestParam Double longitudeDelta) {
        return new ApiResponse<>(bakeryService.getBakeryList(username, sortBy, filterBy, latitude, longitude, latitudeDelta, longitudeDelta));
    }

    @GetMapping("/{bakeryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryDto> getBakery(@CurrentUser String username, @PathVariable Long bakeryId) {
        return new ApiResponse<>(bakeryService.getBakery(username, bakeryId));
    }
}
