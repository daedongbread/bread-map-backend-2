package com.depromeet.breadmapbackend.domain.flag.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.depromeet.breadmapbackend.domain.flag.QFlagBakery.flagBakery;
import static com.depromeet.breadmapbackend.domain.flag.QFlag.flag;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FlagRepositorySupport{
    private final JPAQueryFactory queryFactory;

    public Boolean existFlagBakeryByUserAndBakery(User user, Bakery bakery) {
        Integer fetchOne = queryFactory.selectOne()
                .from(flagBakery)
                .join(flagBakery.flag, flag)
//                .fetchJoin()
                .where(flagBakery.bakery.eq(bakery).and(flag.user.eq(user))).fetchFirst();

        return fetchOne != null;
    }

    public FlagBakery findFlagBakeryByUserAndBakery(User user, Bakery bakery) {
        return queryFactory.selectDistinct(flagBakery)
                .from(flagBakery)
                .join(flagBakery.flag, flag)
//                .fetchJoin()
                .where(flagBakery.bakery.eq(bakery).and(flag.user.eq(user))).fetchFirst();
    }
}
