package com.depromeet.breadmapbackend.web.controller.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminUserListDto {
    private List<AdminUserDto> userDtoList;
    private int totalNum;

    @Builder
    public AdminUserListDto(List<AdminUserDto> userDtoList, int totalNum) {
        this.userDtoList = userDtoList;
        this.totalNum = totalNum;
    }
}
