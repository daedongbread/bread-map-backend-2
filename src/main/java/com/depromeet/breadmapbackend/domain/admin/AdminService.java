package com.depromeet.breadmapbackend.domain.admin;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminJoinRequest;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminLoginRequest;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.domain.admin.dto.*;
import com.depromeet.breadmapbackend.domain.auth.dto.ReissueRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AdminService {
    void adminJoin(AdminJoinRequest request);
    JwtToken adminLogin(AdminLoginRequest request);
    JwtToken reissue(ReissueRequest reissueRequest);
    AdminBarDto getAdminBar();
    AdminImageDto uploadImage(MultipartFile image) throws IOException;
}
