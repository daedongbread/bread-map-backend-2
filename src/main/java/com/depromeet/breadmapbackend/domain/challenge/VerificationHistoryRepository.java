package com.depromeet.breadmapbackend.domain.challenge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VerificationHistoryRepository extends JpaRepository<VerificationHistory, Long> {
    @Query("SELECT vh FROM VerificationHistory vh " +
            "WHERE vh.user.id = :userId " +
            "AND vh.challenge.id = :challengeId " +
            "AND vh.createdAt = :createdAt")
    Boolean findByUserIdAndChallengeIdAndCreatedAt(
            @Param("userId") Long userId,
            @Param("challengeId") Long challengeId,
            @Param("createdAt") LocalDateTime createdAt
    );

}
