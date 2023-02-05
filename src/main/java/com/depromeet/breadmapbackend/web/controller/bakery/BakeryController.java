package com.depromeet.breadmapbackend.web.controller.bakery;

import com.depromeet.breadmapbackend.domain.bakery.BakerySortType;
import com.depromeet.breadmapbackend.service.bakery.BakeryService;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.*;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.SimpleProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
            @RequestParam BakerySortType sortBy,
            @RequestParam Double latitude, @RequestParam Double longitude,
            @RequestParam Double latitudeDelta, @RequestParam Double longitudeDelta) {
        return new ApiResponse<>(bakeryService.findBakeryList(latitude, longitude, latitudeDelta, longitudeDelta, sortBy));
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<BakeryCardDto>> findBakeryListByFilter(
            @CurrentUser String username, @RequestParam BakerySortType sortBy,
            @RequestParam Double latitude, @RequestParam Double longitude,
            @RequestParam Double latitudeDelta, @RequestParam Double longitudeDelta) {
        return new ApiResponse<>(bakeryService.findBakeryListByFilter(username, latitude, longitude, latitudeDelta, longitudeDelta, sortBy));
    }

    @GetMapping("/{bakeryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryDto> findBakery(@CurrentUser String username, @PathVariable Long bakeryId) {
        return new ApiResponse<>(bakeryService.findBakery(username, bakeryId));
    }

    @GetMapping("/{bakeryId}/product")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ProductDto>> findProductList(@PathVariable Long bakeryId) {
        return new ApiResponse<>(bakeryService.findProductList(bakeryId));
    }

    @PostMapping("/report/{bakeryId}/update")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryUpdateReport(@CurrentUser String username, @PathVariable Long bakeryId, @RequestBody BakeryUpdateRequest request) {
        bakeryService.bakeryUpdateReport(username, bakeryId, request);
    }

    @PostMapping("/report/{bakeryId}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryDeleteReport(@CurrentUser String username, @PathVariable Long bakeryId, @RequestPart MultipartFile file) throws IOException {
        bakeryService.bakeryDeleteReport(username, bakeryId, file);
    }

    @PostMapping("/report/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryAddReport(@CurrentUser String username, @RequestBody BakeryReportRequest request) {
        bakeryService.bakeryAddReport(username, request);
    }

    @PostMapping("/report/{bakeryId}/image")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryReportImage(@CurrentUser String username, @PathVariable Long bakeryId, @RequestPart List<MultipartFile> files) throws IOException {
        bakeryService.bakeryReportImage(username, bakeryId, files);
    }

    @PostMapping("/report/{bakeryId}/product")
    @ResponseStatus(HttpStatus.CREATED)
    public void productAddReport(
            @CurrentUser String username, @PathVariable Long bakeryId, @RequestPart ProductReportRequest request,
            @RequestPart(required = false) List<MultipartFile> files) throws IOException {
        bakeryService.productAddReport(username, bakeryId, request, files);
    }

    @GetMapping("/{bakeryId}/review/product/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<SimpleProductDto>> searchSimpleProductList(
            @PathVariable Long bakeryId,
            @RequestParam
            @NotBlank(message = "검색어는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
            @Size(min=1, max=20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
            String name) {
        return new ApiResponse<>(bakeryService.searchSimpleProductList(bakeryId, name));
    }
}
