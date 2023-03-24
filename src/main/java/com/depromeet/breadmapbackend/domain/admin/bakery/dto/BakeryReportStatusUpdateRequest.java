package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryReportStatusUpdateRequest {
    private BakeryAddReportStatus status;
}
