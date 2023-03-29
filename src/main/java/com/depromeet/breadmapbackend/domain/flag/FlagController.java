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
    public ApiResponse<List<FlagDto>> findFlags(@PathVariable Long userId) {
        return new ApiResponse<>(flagService.findFlags(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addFlag(@CurrentUser String username, @RequestBody @Validated(ValidationSequence.class) FlagRequest request) {
        flagService.addFlag(username, request);
    }

    @PatchMapping("/{flagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFlag(
            @CurrentUser String username, @PathVariable Long flagId, @RequestBody @Validated(ValidationSequence.class) FlagRequest request) {
        flagService.updateFlag(username, flagId, request);
    }

    @DeleteMapping("/{flagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFlag(@CurrentUser String username, @PathVariable Long flagId) {
        flagService.removeFlag(username, flagId);
    }

    @GetMapping("/{flagId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<FlagBakeryDto> findBakeryByFlag(@PathVariable Long flagId) {
        return new ApiResponse<>(flagService.findBakeryByFlag(flagId));
    }

    @PostMapping("/{flagId}/bakeries/{bakeryId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBakeryToFlag(@CurrentUser String username, @PathVariable Long flagId, @PathVariable Long bakeryId) {
        flagService.addBakeryToFlag(username, flagId, bakeryId);
    }

    @DeleteMapping("/{flagId}/bakeries/{bakeryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBakeryToFlag(@CurrentUser String username, @PathVariable Long flagId, @PathVariable Long bakeryId) {
        flagService.removeBakeryToFlag(username, flagId, bakeryId);
    }
}
