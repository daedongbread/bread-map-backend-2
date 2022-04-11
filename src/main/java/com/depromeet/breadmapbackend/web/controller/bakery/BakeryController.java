package com.depromeet.breadmapbackend.web.controller.bakery;

import com.depromeet.breadmapbackend.service.bakery.BakeryService;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bakery")
@RequiredArgsConstructor
public class BakeryController {
    private final BakeryService bakeryService;

    @GetMapping
    public ApiResponse<List<BakeryCardDto>> getBakeryList(
            @RequestParam Double latitude, @RequestParam Double longitude,
            @RequestParam Double height, @RequestParam Double width) {
        return new ApiResponse<>(bakeryService.getBakeryList(latitude, longitude, height, width));
    }
}
