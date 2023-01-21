package com.depromeet.breadmapbackend.web.controller.review.dto;

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
public class ReviewCommentRequest {
    @NotBlank(message = "내용은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=10, max=500, message = "10자 이상, 500자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String content;
    @NotBlank(message = "부모 댓글 고유 번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private Long parentCommentId;
}
