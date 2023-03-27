package com.depromeet.breadmapbackend.domain.bakery.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryReportImageRequest {
    private List<String> images;
}
