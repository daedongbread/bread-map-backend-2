package com.depromeet.breadmapbackend.domain.flag.repository;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;
import com.depromeet.breadmapbackend.domain.flag.Flag;

public class FlagRepositoryImpl extends MyQuerydslRepositorySupport implements FlagRepositoryCustom {

    public FlagRepositoryImpl() {super(Flag.class);}

}
