package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class BakeryReviewRepositoryImpl extends MyQuerydslRepositorySupport implements BakeryReviewRepositoryCustom {

    public BakeryReviewRepositoryImpl() {super(BakeryReview.class);}

}
