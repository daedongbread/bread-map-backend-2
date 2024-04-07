package com.depromeet.breadmapbackend.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationHistory {
    private long userId;
    private long challengeId;
    private String challengeTitle;

}
