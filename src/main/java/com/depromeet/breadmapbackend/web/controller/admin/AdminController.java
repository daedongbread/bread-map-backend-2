package com.depromeet.breadmapbackend.web.controller.admin;

import com.depromeet.breadmapbackend.service.admin.AdminService;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
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
    public ApiResponse<List<AdminAllBakeryDto>> getAllBakeryList(){
        return new ApiResponse<>(adminService.getAllBakeryList());
    }

    @GetMapping("/getBakery/{bakeryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminBakeryDto> getBakeryDetail(@PathVariable Long bakeryId) {
        return new ApiResponse<>(adminService.getBakeryDetail(bakeryId));
    }
}
