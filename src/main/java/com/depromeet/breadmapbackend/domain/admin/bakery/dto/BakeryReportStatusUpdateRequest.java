package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReportStatus;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryReportStatusUpdateRequest {
    @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
    private BakeryAddReportStatus status;
}
