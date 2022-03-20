package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class BakeryRepositoryImpl extends MyQuerydslRepositorySupport implements BakeryRepositoryCustom {

    public BakeryRepositoryImpl() {super(Bakery.class);}


}
