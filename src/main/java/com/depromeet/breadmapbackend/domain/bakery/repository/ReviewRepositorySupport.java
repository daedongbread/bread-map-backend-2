package com.depromeet.breadmapbackend.domain.bakery.repository;


import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.depromeet.breadmapbackend.domain.bakery.QBakery.bakery;
import static com.depromeet.breadmapbackend.domain.review.QBreadReview.breadReview;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public List<SimpleReviewDto> getAllReviewList() {
        return queryFactory.select(Projections.fields(SimpleReviewDto.class,
                        breadReview.id,
                        breadReview.content))
                        .from(breadReview)
                        .fetch();
    }

}
