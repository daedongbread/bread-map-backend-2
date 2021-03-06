package com.depromeet.breadmapbackend.web.controller.bakery;

import com.depromeet.breadmapbackend.domain.bakery.BakerySortType;
import com.depromeet.breadmapbackend.service.bakery.BakeryService;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.*;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bakery")
@RequiredArgsConstructor
public class BakeryController {
    private final BakeryService bakeryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<BakeryCardDto>> findBakeryList(
            @RequestParam BakerySortType sort,
            @RequestParam Double latitude, @RequestParam Double longitude,
            @RequestParam Double latitudeDelta, @RequestParam Double longitudeDelta) {
        return new ApiResponse<>(bakeryService.findBakeryList(latitude, longitude, latitudeDelta, longitudeDelta, sort));
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<BakeryFilterCardDto>> findBakeryListByFilter(
            @CurrentUser String username, @RequestParam BakerySortType sort,
            @RequestParam Double latitude, @RequestParam Double longitude,
            @RequestParam Double latitudeDelta, @RequestParam Double longitudeDelta) {
        return new ApiResponse<>(bakeryService.findBakeryListByFilter(username, latitude, longitude, latitudeDelta, longitudeDelta, sort));
    }

    @GetMapping("/{bakeryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryDto> findBakery(@PathVariable Long bakeryId) {
        return new ApiResponse<>(bakeryService.findBakery(bakeryId));
    }

    @PostMapping("/report/{bakeryId}/update")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryUpdateReport(@PathVariable Long bakeryId, @RequestBody BakeryUpdateRequest request) {
        bakeryService.bakeryUpdateReport(bakeryId, request);
    }

    @PostMapping("/report/{bakeryId}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryDeleteReport(@PathVariable Long bakeryId, @RequestPart MultipartFile file) throws IOException {
        bakeryService.bakeryDeleteReport(bakeryId, file);
    }

    @PostMapping("/report/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryAddReport(@RequestBody BakeryReportRequest request) {
        bakeryService.bakeryAddReport(request);
    }

    @PostMapping("/report/{bakeryId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void breadAddReport(@PathVariable Long bakeryId, @RequestPart BreadReportRequest request,
                               @RequestPart List<MultipartFile> files) throws IOException {
        bakeryService.breadAddReport(bakeryId, request, files);
    }
}
