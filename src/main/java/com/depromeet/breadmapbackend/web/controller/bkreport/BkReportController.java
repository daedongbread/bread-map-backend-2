package com.depromeet.breadmapbackend.web.controller.bkreport;

import com.depromeet.breadmapbackend.service.bkreport.BkReportService;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.bkreport.dto.AddBakeryReportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class BkReportController {
    private final BkReportService bkreportService;

    @PostMapping("/addBakeryReport")
    @ResponseStatus(HttpStatus.OK)
    public void addBakeryReport(@CurrentUser String username, @RequestBody AddBakeryReportRequest request) {
        bkreportService.addBakeryReport(username, request);
    }
}
