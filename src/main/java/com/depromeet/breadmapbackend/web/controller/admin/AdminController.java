package com.depromeet.breadmapbackend.web.controller.admin;

import com.depromeet.breadmapbackend.domain.admin.AdminBakeryImageType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.service.admin.AdminService;
import com.depromeet.breadmapbackend.web.advice.ValidationSequence;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.common.PageableSortConverter;
import com.depromeet.breadmapbackend.web.controller.user.dto.ReissueRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public void adminJoin(@RequestBody @Validated(ValidationSequence.class) AdminJoinRequest request) {
        adminService.adminJoin(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<JwtToken> adminLogin(@RequestBody @Validated(ValidationSequence.class) AdminLoginRequest request) {
        return new ApiResponse<>(adminService.adminLogin(request));
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<JwtToken> reissue(@RequestBody @Validated(ValidationSequence.class) ReissueRequest request) {
        return new ApiResponse<>(adminService.reissue(request));
    }

    @GetMapping("/bar")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminBarDto> getAdminBar() {
        return new ApiResponse<>(adminService.getAdminBar());
    }
    

    @GetMapping("/bakery")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<AdminSimpleBakeryDto>> getBakeryList(@RequestParam int page) {
        return new ApiResponse<>(adminService.getBakeryList(page));
    }

    @GetMapping("/bakery/{bakeryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminBakeryDto> getBakery(@PathVariable Long bakeryId) {
        return new ApiResponse<>(adminService.getBakery(bakeryId));
    }

    @GetMapping("/bakery/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<AdminSimpleBakeryDto>> searchBakeryList(
            @RequestParam String name, @RequestParam int page) {
        return new ApiResponse<>(adminService.searchBakeryList(name, page));
    }

    @GetMapping("/bakery/location")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryLocationDto> getBakeryLatitudeLongitude(@RequestParam String address) {
        return new ApiResponse<>(adminService.getBakeryLatitudeLongitude(address));
    }

    @PostMapping("/bakery")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBakery(@RequestBody @Validated(ValidationSequence.class) BakeryAddRequest request) {
        adminService.addBakery(request);
    }

    @PostMapping("/bakery/{bakeryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBakery(
            @PathVariable Long bakeryId, @RequestBody @Validated(ValidationSequence.class) BakeryUpdateRequest request) {
        adminService.updateBakery(bakeryId, request);
    }

    @DeleteMapping("/bakery/{bakeryId}/product/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable Long bakeryId, @PathVariable Long productId) {
        adminService.deleteProduct(bakeryId, productId);
    }

    @GetMapping("/bakery/{bakeryId}/{imageType}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<AdminImageDto>> getAdminImages(
            @PathVariable Long bakeryId, @PathVariable AdminBakeryImageType imageType, @RequestParam int page) {
        return new ApiResponse<>(adminService.getAdminImages(bakeryId, imageType, page));
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> downloadAdminImage(@RequestParam String image) throws IOException {
        return adminService.downloadAdminImage(image);
    }
    
    @DeleteMapping("/bakery/{bakeryId}/{imageType}/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminImage(@PathVariable Long bakeryId, @PathVariable AdminBakeryImageType imageType, @PathVariable Long imageId) {
        adminService.deleteAdminImage(bakeryId, imageType, imageId);
    }

    @GetMapping("/bakery/{bakeryId}/productAddReport")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<ProductAddReportDto>> getProductAddReports(
            @PathVariable Long bakeryId, @RequestParam int page) {
        return new ApiResponse<>(adminService.getProductAddReports(bakeryId, page));
    }

    @PatchMapping("/bakery/{bakeryId}/productAddReport/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registerProductAddImage(
            @PathVariable Long bakeryId, @PathVariable Long reportId,
            @RequestBody @Validated(ValidationSequence.class) ProductAddImageRegisterRequest request) {
        adminService.registerProductAddImage(bakeryId, reportId, request);
    }

    @DeleteMapping("/bakery/{bakeryId}/productAddReport/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductAddReport(@PathVariable Long bakeryId, @PathVariable Long reportId) {
        adminService.deleteProductAddReport(bakeryId, reportId);
    }

    @GetMapping("/bakery/{bakeryId}/updateReport")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<BakeryUpdateReportDto>> getBakeryUpdateReports(
            @PathVariable Long bakeryId, @RequestParam int page) {
        return new ApiResponse<>(adminService.getBakeryUpdateReports(bakeryId, page));
    }

    @PatchMapping("/bakery/{bakeryId}/updateReport/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeBakeryUpdateReport(@PathVariable Long bakeryId, @PathVariable Long reportId) {
        adminService.changeBakeryUpdateReport(bakeryId, reportId);
    }

    @DeleteMapping("/bakery/{bakeryId}/updateReport/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBakeryUpdateReport(@PathVariable Long bakeryId, @PathVariable Long reportId) {
        adminService.deleteBakeryUpdateReport(bakeryId, reportId);
    }

    @DeleteMapping("/bakery/{bakeryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBakery(@PathVariable Long bakeryId) {
        adminService.deleteBakery(bakeryId);
    }

    @GetMapping("/bakery/report")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<SimpleBakeryAddReportDto>> getBakeryAddReportList(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminService.getBakeryAddReportList(PageableSortConverter.convertSort(pageable)));
    }

    @GetMapping("/bakery/report/{reportId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryAddReportDto> getBakeryAddReport(@PathVariable Long reportId) {
        return new ApiResponse<>(adminService.getBakeryAddReport(reportId));
    }

    @PatchMapping("/bakery/report/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBakeryAddReportStatus(
            @PathVariable Long reportId, @RequestBody @Validated(ValidationSequence.class) BakeryReportStatusUpdateRequest request) {
        adminService.updateBakeryAddReportStatus(reportId, request);
    }

    @GetMapping("/review/report")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<AdminReviewReportDto>> getReviewReportList(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminService.getReviewReportList(PageableSortConverter.convertSort(pageable)));
    }

    @PatchMapping("/review/report/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateReviewStatus(@PathVariable Long reportId) {
        adminService.updateReviewStatus(reportId);
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<AdminUserDto>> getUserList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminService.getUserList(PageableSortConverter.convertSort(pageable)));
    }

    @PatchMapping("/user/{userId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeUserBlock(@PathVariable Long userId) {
        adminService.changeUserBlock(userId);
    }

    @PostMapping("/image")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TempImageDto> uploadTempImage(@RequestPart MultipartFile file) throws IOException {
        return new ApiResponse<>(adminService.uploadTempImage(file));
    }
}
