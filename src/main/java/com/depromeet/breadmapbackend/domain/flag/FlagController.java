package com.depromeet.breadmapbackend.domain.flag;

import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagBakeryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/flags")
@RequiredArgsConstructor
public class FlagController {
    private final FlagService flagService;

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<FlagDto>> getFlags(@PathVariable Long userId) {
        return new ApiResponse<>(flagService.getFlags(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addFlag(@CurrentUser String oAuthId, @RequestBody @Validated(ValidationSequence.class) FlagRequest request) {
        flagService.addFlag(oAuthId, request);
    }

    @PatchMapping("/{flagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFlag(
            @CurrentUser String oAuthId, @PathVariable Long flagId, @RequestBody @Validated(ValidationSequence.class) FlagRequest request) {
        flagService.updateFlag(oAuthId, flagId, request);
    }

    @DeleteMapping("/{flagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFlag(@CurrentUser String oAuthId, @PathVariable Long flagId) {
        flagService.removeFlag(oAuthId, flagId);
    }

    @GetMapping("/{flagId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<FlagBakeryDto> getBakeryByFlag(@CurrentUser String oAuthId, @PathVariable Long flagId) {
        return new ApiResponse<>(flagService.getBakeryByFlag(oAuthId, flagId));
    }

    @PostMapping("/{flagId}/bakeries/{bakeryId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBakeryToFlag(@CurrentUser String oAuthId, @PathVariable Long flagId, @PathVariable Long bakeryId) {
        flagService.addBakeryToFlag(oAuthId, flagId, bakeryId);
    }

    @DeleteMapping("/{flagId}/bakeries/{bakeryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBakeryToFlag(@CurrentUser String oAuthId, @PathVariable Long flagId, @PathVariable Long bakeryId) {
        flagService.removeBakeryToFlag(oAuthId, flagId, bakeryId);
    }
}
