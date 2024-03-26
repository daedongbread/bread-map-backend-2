package com.depromeet.breadmapbackend.domain.challenge;

public interface SubmissionService {
    boolean checkChallengeSubmissionToday(long userId, long challengeId);

}
