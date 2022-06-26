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

    @PostMapping("/{bakeryId}/update")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryUpdate(@PathVariable Long bakeryId, @RequestBody BakeryUpdateRequest request) {
        bakeryService.bakeryUpdate(bakeryId, request);
    }

    @PostMapping("/{bakeryId}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryDelete(@PathVariable Long bakeryId, @RequestPart MultipartFile file) throws IOException {
        bakeryService.bakeryDelete(bakeryId, file);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryReport(@RequestBody BakeryReportRequest request) {
        bakeryService.bakeryReport(request);
    }

    @PostMapping("/{bakeryId}/report")
    @ResponseStatus(HttpStatus.CREATED)
    public void breadReport(@PathVariable Long bakeryId, @RequestBody BreadReportRequest request,
                            List<MultipartFile> files) throws IOException {
        bakeryService.breadReport(bakeryId, request, files);
    }
}
