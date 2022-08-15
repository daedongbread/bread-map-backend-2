package com.depromeet.breadmapbackend.web.controller.admin;

import com.depromeet.breadmapbackend.service.admin.AdminService;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/bakery/all")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminBakeryListDto> getBakeryList(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminService.getBakeryList(pageable));
    }

    @GetMapping("/bakery/{bakeryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminBakeryDto> getBakery(@PathVariable Long bakeryId) {
        return new ApiResponse<>(adminService.getBakery(bakeryId));
    }

    @GetMapping("/bakery/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminBakeryListDto> searchBakeryList(
            @RequestParam String name,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminService.searchBakeryList(name, pageable));
    }

    @GetMapping("/bakery/location")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryLocationDto> getBakeryLatitudeLongitude(@RequestParam String address) throws JsonProcessingException {
        return new ApiResponse<>(adminService.getBakeryLatitudeLongitude(address));
    }


    @PostMapping("/bakery")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBakery(
            @RequestPart AddBakeryRequest request,
            @RequestPart MultipartFile bakeryImage, @RequestPart List<MultipartFile> breadImageList) throws IOException {
        adminService.addBakery(request, bakeryImage, breadImageList);
    }

    @PostMapping("/bakery/{bakeryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBakery(
            @PathVariable Long bakeryId, @RequestPart UpdateBakeryRequest request,
            @RequestPart MultipartFile bakeryImage, @RequestPart List<MultipartFile> breadImageList) throws IOException {
        adminService.updateBakery(bakeryId, request, bakeryImage, breadImageList);
    }

    @DeleteMapping("/bakery/{bakeryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBakery(@PathVariable Long bakeryId) {
        adminService.deleteBakery(bakeryId);
    }

    @GetMapping("/bakery/report/all")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryAddReportListDto> getBakeryReportList(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminService.getBakeryReportList(pageable));
    }

    @GetMapping("/bakery/report/{reportId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryAddReportDto> getBakeryReport(@PathVariable Long reportId) {
        return new ApiResponse<>(adminService.getBakeryReport(reportId));
    }

    @PatchMapping("/bakery/report/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBakeryAddReportStatus(@PathVariable Long reportId, @RequestBody UpdateBakeryReportStatusRequest request) {
        adminService.updateBakeryAddReportStatus(reportId, request);
    }

    @GetMapping("/review/report/all")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminReviewReportListDto> getReviewReportList(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminService.getReviewReportList(pageable));
    }

    @PatchMapping("/review/report/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateReviewStatus(@PathVariable Long reportId) {
        adminService.updateReviewStatus(reportId);
    }

    @GetMapping("/user/all")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminUserListDto> getUserList(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminService.getUserList(pageable));
    }

    @PatchMapping("/user/{userId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeUserBlock(@PathVariable Long userId) {
        adminService.changeUserBlock(userId);
    }
}
