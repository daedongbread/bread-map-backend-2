package com.depromeet.breadmapbackend.domain.challenge;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChallengeQuery {

    private final JPAQueryFactory queryFactory;



}
