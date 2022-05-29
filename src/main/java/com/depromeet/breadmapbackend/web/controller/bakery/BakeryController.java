package com.depromeet.breadmapbackend.web.controller.bakery;

import com.depromeet.breadmapbackend.domain.bakery.SortType;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.service.bakery.BakeryService;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryInfo;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BreadDto;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bakery")
@RequiredArgsConstructor
public class BakeryController {
    private final BakeryService bakeryService;

    @GetMapping("/default")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<BakeryCardDto>> findBakeryList(
            @RequestParam SortType sort,
            @RequestParam Double latitude, @RequestParam Double longitude,
            @RequestParam Double latitudeDelta, @RequestParam Double longitudeDelta) {
        return new ApiResponse<>(bakeryService.findBakeryList(latitude, longitude, latitudeDelta, longitudeDelta, sort));
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<BakeryCardDto>> findBakeryListByFilter(
            @CurrentUser String username, @RequestParam SortType sort,
            @RequestParam Double latitude, @RequestParam Double longitude,
            @RequestParam Double latitudeDelta, @RequestParam Double longitudeDelta) {
        return new ApiResponse<>(bakeryService.findBakeryListByFilter(username, latitude, longitude, latitudeDelta, longitudeDelta, sort));
    }

    @GetMapping("/{bakeryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryDto> findBakery(@PathVariable Long bakeryId) {
        return new ApiResponse<>(bakeryService.findBakery(bakeryId));
    }

    @PatchMapping("/{bakeryId}/heart")
    @ResponseStatus(HttpStatus.OK)
    public void heartToBakery(@CurrentUser String username, @PathVariable Long bakeryId) {
        bakeryService.heartToBakery(username, bakeryId);
    }

    @PatchMapping("/{bakeryId}/unheart")
    @ResponseStatus(HttpStatus.OK)
    public void unHeartToBakery(@CurrentUser String username, @PathVariable Long bakeryId) {
        bakeryService.unHeartToBakery(username, bakeryId);
    }

    @PatchMapping("/{bakeryId}/flag")
    @ResponseStatus(HttpStatus.OK)
    public void flagToBakery(@CurrentUser String username, @PathVariable Long bakeryId) {
        bakeryService.flagToBakery(username, bakeryId);
    }

    @PatchMapping("/{bakeryId}/unflag")
    @ResponseStatus(HttpStatus.OK)
    public void unFlagToBakery(@CurrentUser String username, @PathVariable Long bakeryId) {
        bakeryService.unFlagToBakery(username, bakeryId);
    }
}
