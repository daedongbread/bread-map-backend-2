package com.depromeet.breadmapbackend.domain.admin.openSearch.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
@Documented
@Constraint(validatedBy = LowerCaseValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LowerCase {
    String message() default "Value must be in lowercase";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
