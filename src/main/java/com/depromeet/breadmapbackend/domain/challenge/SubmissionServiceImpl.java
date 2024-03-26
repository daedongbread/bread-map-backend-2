package com.depromeet.breadmapbackend.domain.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
    private final VerificationHistoryRepository verificationHistoryRepository;

    @Override
    public boolean checkChallengeSubmissionToday(long userId, long challengeId) {
        return verificationHistoryRepository.findByUserIdAndChallengeIdAndCreatedAt(userId, challengeId, LocalDateTime.now());
    }
}
