package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Menu;
import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class MenuRepositoryImpl extends MyQuerydslRepositorySupport implements MenuRepositoryCustom {

    public MenuRepositoryImpl() {super(Menu.class);}


}
