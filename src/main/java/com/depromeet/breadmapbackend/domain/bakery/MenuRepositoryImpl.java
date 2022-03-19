package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class MenuRepositoryImpl extends MyQuerydslRepositorySupport implements MenuRepositoryCustom {

    public MenuRepositoryImpl() {super(Menu.class);}


}
