package com.depromeet.breadmapbackend.web.controller.common;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {}