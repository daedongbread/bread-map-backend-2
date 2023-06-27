package com.depromeet.breadmapbackend.global.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = {EnumValidator.class})
@Target({TYPE_USE, METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumCheck {
	String message() default "invalid enum type";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
