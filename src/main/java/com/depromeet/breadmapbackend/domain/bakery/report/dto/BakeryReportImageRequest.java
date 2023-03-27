package com.depromeet.breadmapbackend.domain.bakery.report.dto;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryReportImageRequest {
    @NotEmpty(message = "이미지들은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private List<String> images;
}
