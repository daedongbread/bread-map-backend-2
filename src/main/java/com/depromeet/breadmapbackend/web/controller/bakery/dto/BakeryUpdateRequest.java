package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.BakeryUpdateReason;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryUpdateRequest {
    private BakeryUpdateReason reason;

    @NotBlank(message = "수정 사항은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(max=150, message = "150자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String content;
}
