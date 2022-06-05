package com.depromeet.breadmapbackend.web.controller.flag;

import com.depromeet.breadmapbackend.service.flag.FlagService;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.flag.dto.AddFlagRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/flag")
@RequiredArgsConstructor
public class FlagController {
    private final FlagService flagService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addFlag(@CurrentUser String username, @RequestBody AddFlagRequest request) {
        flagService.addFlag(username, request);
    }

    @DeleteMapping("/{flagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFlag(@CurrentUser String username, @PathVariable Long flagId) {
        flagService.removeFlag(username, flagId);
    }

    @PatchMapping("/{flagId}")
    @ResponseStatus(HttpStatus.OK)
    public void addBakeryToFlag(@CurrentUser String username, @PathVariable Long flagId, @RequestParam Long bakeryId) {
        flagService.addBakeryToFlag(username, flagId, bakeryId);
    }

    @DeleteMapping("/{flagId}/{flagBakeryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBakeryToFlag(@CurrentUser String username, @PathVariable Long flagId, @PathVariable Long flagBakeryId) {
        flagService.removeBakeryToFlag(username, flagId, flagBakeryId);
    }
}
