package com.depromeet.breadmapbackend.global.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasLoginAnnotation = parameter.hasParameterAnnotation(CurrentUser.class);
		boolean hasMemberType = String.class.isAssignableFrom(parameter.getParameterType());
		return hasLoginAnnotation && hasMemberType;
	}

	@Override
	public Object resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory
	) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null)
			return null;

		return CurrentUserInfo.class.cast(authentication.getPrincipal()).getDelimiterValue();
	}

}