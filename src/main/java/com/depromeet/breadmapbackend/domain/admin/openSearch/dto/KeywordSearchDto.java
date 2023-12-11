package com.depromeet.breadmapbackend.domain.admin.openSearch.dto;

import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordSearchDto {
    private User user;
    private String keyword;
}
