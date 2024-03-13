package com.depromeet.breadmapbackend.domain.challenge;

public interface ChallengeService {

    /**
     * 챌린지 참가
     */
    void participate(String oAuthId, Long challengeId);

}
