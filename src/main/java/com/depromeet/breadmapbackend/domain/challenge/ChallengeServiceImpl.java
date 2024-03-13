package com.depromeet.breadmapbackend.domain.challenge;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeParticipantRepository challengeParticipantRepository;

    private final ChallengeQuery challengeQuery;

    /**
     * 챌린지 참가
     */
    @Override
    public void participate(long challengeId, String oAuthId) {
        var challenge = challengeQuery.getChallengeWithUserOAuthId(challengeId, oAuthId)
                .orElseThrow(() -> new DaedongException(DaedongStatus.CHALLENGE_NOT_FOUND));

        if (!CollectionUtils.isEmpty(challenge.getParticipants())) {
            throw new DaedongException(DaedongStatus.ALREADY_PARTICIPATED_CHALLENGE);
        }

        challengeParticipantRepository.save(ChallengeParticipant.builder()
                .challenge(challenge)
                .user(challenge.getParticipants().get(0).getUser())
                .build());
    }
}
