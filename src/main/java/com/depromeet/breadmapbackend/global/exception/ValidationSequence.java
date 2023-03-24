package com.depromeet.breadmapbackend.global.exception;

import javax.validation.GroupSequence;

import static com.depromeet.breadmapbackend.global.exception.ValidationGroups.*;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@GroupSequence({NotEmptyGroup.class, SizeCheckGroup.class, PatternCheckGroup.class })
public interface ValidationSequence {
}
