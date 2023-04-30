package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminImageRegisterRequest {
    @NotEmpty(message = "이미지 리스트는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private List<Long> imageIdList;
}
