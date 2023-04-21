package com.depromeet.breadmapbackend.domain.auth.dto;

import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
    private OAuthType type;

    @NotBlank(message = "ID Token은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String idToken;

    @NotNull(message = "서비스 이용약관 동의 여부는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @AssertTrue(message = "서비스 이용약관 동의 여부는 반드시 참이어야 합니다.", groups = ValidationGroups.PatternCheckGroup.class)
    private Boolean isTermsOfServiceAgreed;

    @NotNull(message = "개인정보 수집 및 이용 동의 여부는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @AssertTrue(message = "개인정보 수집 및 이용 동의 여부는 반드시 참이어야 합니다.", groups = ValidationGroups.PatternCheckGroup.class)
    private Boolean isPersonalInfoCollectionAgreed;

    @NotNull(message = "마케팅 정보 수신 동의 여부는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private Boolean isMarketingInfoReceptionAgreed;
//    @JsonIgnore
//    public boolean isFullyAgree() {
//        return isTermsOfServiceAgreed && isPersonalInfoCollectionAgreed;
//    }
}
