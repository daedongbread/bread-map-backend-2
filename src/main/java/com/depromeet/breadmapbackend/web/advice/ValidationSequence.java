package com.depromeet.breadmapbackend.web.advice;

import javax.validation.GroupSequence;

import static com.depromeet.breadmapbackend.web.advice.ValidationGroups.*;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@GroupSequence({NotEmptyGroup.class, SizeCheckGroup.class, PatternCheckGroup.class })
public interface ValidationSequence {
}
