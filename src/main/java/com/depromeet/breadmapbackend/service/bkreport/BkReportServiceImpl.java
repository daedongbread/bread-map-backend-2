package com.depromeet.breadmapbackend.service.bkreport;

import com.depromeet.breadmapbackend.domain.bkreport.BkReport;
import com.depromeet.breadmapbackend.domain.bkreport.BkReportRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.bkreport.dto.AddBakeryReportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BkReportServiceImpl implements BkReportService{
    private final BkReportRepository bkReportRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addBakeryReport(String username, AddBakeryReportRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        BkReport bkReport = BkReport.builder()
                .user(user).bakeryName(request.getBakeryName()).address(request.getAddress())
                .reason(request.getReason()).status(0)
                .build();

        bkReportRepository.save(bkReport);
    }
}
