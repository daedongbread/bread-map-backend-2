package com.depromeet.breadmapbackend.global.annotation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class EnumValidator implements ConstraintValidator<EnumCheck, Enum<?>> {
    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext context) {
        return value != null;
    }
}
