package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.web.controller.admin.dto.*;

import java.util.List;

public interface AdminService {
    List<AdminAllBakeryDto> getAllBakeryList();
    AdminBakeryDto getBakeryDetail(Long bakeryId);
}
