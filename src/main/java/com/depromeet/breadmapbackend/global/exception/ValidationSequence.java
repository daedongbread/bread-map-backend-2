package com.depromeet.breadmapbackend.global.exception;

import static com.depromeet.breadmapbackend.global.exception.ValidationGroups.*;

import javax.validation.GroupSequence;

@GroupSequence({NotEmptyGroup.class, SizeCheckGroup.class, PatternCheckGroup.class})
public interface ValidationSequence {
}
