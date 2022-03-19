package com.depromeet.breadmapbackend.domain.flag;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class FlagRepositoryImpl extends MyQuerydslRepositorySupport implements FlagRepositoryCustom {

    public FlagRepositoryImpl() {super(Flag.class);}


}
