package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class BakeryRepositoryImpl extends MyQuerydslRepositorySupport implements BakeryRepositoryCustom {

    public BakeryRepositoryImpl() {super(Bakery.class);}


}
