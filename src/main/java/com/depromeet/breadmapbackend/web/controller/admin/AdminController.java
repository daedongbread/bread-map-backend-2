package com.depromeet.breadmapbackend.web.controller.admin;

import com.depromeet.breadmapbackend.service.admin.AdminService;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryReportDto;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/getAllBakery")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<AdminAllBakeryDto>> getAllBakeryList() {
        return new ApiResponse<>(adminService.getAllBakeryList());
    }

    @GetMapping("/getBakery/{bakeryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminBakeryDto> getBakeryDetail(@PathVariable Long bakeryId) {
        return new ApiResponse<>(adminService.getBakeryDetail(bakeryId));
    }

    @PostMapping("/addBakery")
    @ResponseStatus(HttpStatus.OK)
    public void addBakery(@CurrentUser String username, @RequestBody AddBakeryRequest request) {
        adminService.addBakery(username, request);
    }

    @GetMapping("/getAllBakeryReport")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<BakeryReportDto>> getAllBakeryReport() {
        return new ApiResponse<>(adminService.getAllBakeryReport());
    }

    @GetMapping("/getBakeryReport/{reportId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryReportDto> getBakeryReportDetail(@PathVariable Long reportId) {
        return new ApiResponse<>(adminService.getBakeryReportDetail(reportId));
    }

    @PostMapping("/updateBakeryReport/{reportId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBakeryReport(@PathVariable Long reportId, @RequestBody UpdateBakeryReportStatusRequest request) {
        adminService.updateBakeryReport(reportId, request);
    }
}
