package com.depromeet.breadmapbackend.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeSubmission {
    private Long userId;
    private Long challengeId;
    private List<ChallengeSubmission> challengeSubmissions;
}
