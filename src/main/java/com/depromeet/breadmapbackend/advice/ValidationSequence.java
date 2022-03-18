package com.depromeet.breadmapbackend.advice;

import com.depromeet.breadmapbackend.advice.ValidationGroups.*;
import javax.validation.GroupSequence;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@GroupSequence({NotEmptyGroup.class, SizeCheckGroup.class, PatternCheckGroup.class })
public interface ValidationSequence {
}
