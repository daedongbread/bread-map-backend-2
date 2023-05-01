package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBakeryIsNewDto {
    private Boolean adminImageIsNew;
    private Boolean productAddReportIsNew;
    private Boolean bakeryUpdateReportIsNew;
    private Boolean newReviewIsNew;
}
