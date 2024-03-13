package com.depromeet.breadmapbackend.domain.challenge;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.depromeet.breadmapbackend.domain.challenge.QChallenge.challenge;
import static com.depromeet.breadmapbackend.domain.challenge.QChallengeParticipant.challengeParticipant;
import static com.depromeet.breadmapbackend.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class ChallengeQuery {

    private final JPAQueryFactory queryFactory;

    /**
     * user oauth id로 챌린지 참가 가능한지 조회
     */
    public Optional<Challenge> getChallengeWithUserOAuthId(long challengeId, String oAuthId) {
        Challenge c = queryFactory
                .selectFrom(challenge)
                .leftJoin(challenge.participants, challengeParticipant)
                .join(challengeParticipant.user, user)
                .where(
                        challenge.id.eq(challengeId),
                        user.oAuthInfo.oAuthId.eq(oAuthId)
                )
                .fetchOne();
        return Optional.ofNullable(c);
    }

}
