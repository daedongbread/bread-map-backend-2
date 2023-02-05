package com.depromeet.breadmapbackend.web.controller.flag;

import com.depromeet.breadmapbackend.service.flag.FlagService;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagBakeryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/flag")
@RequiredArgsConstructor
public class FlagController {
    private final FlagService flagService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<FlagDto>> findFlags(@CurrentUser String username) {
        return new ApiResponse<>(flagService.findFlags(username));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addFlag(@CurrentUser String username, @RequestBody FlagRequest request) {
        flagService.addFlag(username, request);
    }

    @PatchMapping("/{flagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFlag(@CurrentUser String username, @PathVariable Long flagId, @RequestBody FlagRequest request) {
        flagService.updateFlag(username, flagId, request);
    }

    @DeleteMapping("/{flagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFlag(@CurrentUser String username, @PathVariable Long flagId) {
        flagService.removeFlag(username, flagId);
    }
    @GetMapping("/{flagId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<FlagBakeryDto>> findBakeryByFlag(@CurrentUser String username, @PathVariable Long flagId) {
        return new ApiResponse<>(flagService.findBakeryByFlag(username, flagId));
    }

    @PostMapping("/{flagId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBakeryToFlag(@CurrentUser String username, @PathVariable Long flagId, @RequestParam Long bakeryId) {
        flagService.addBakeryToFlag(username, flagId, bakeryId);
    }

    @DeleteMapping("/{flagId}/bakery/{bakeryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBakeryToFlag(@CurrentUser String username, @PathVariable Long flagId, @PathVariable Long bakeryId) {
        flagService.removeBakeryToFlag(username, flagId, bakeryId);
    }
}
