package com.depromeet.breadmapbackend.global.infra;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.depromeet.breadmapbackend.global.converter.param.AdminBakeryFilterConverter;
import com.depromeet.breadmapbackend.global.converter.param.AdminBakeryImageTypeConverter;
import com.depromeet.breadmapbackend.global.converter.param.BakerySortTypeConverter;
import com.depromeet.breadmapbackend.global.converter.param.FeedStatusConverter;
import com.depromeet.breadmapbackend.global.converter.param.FeedTypeConverter;
import com.depromeet.breadmapbackend.global.converter.param.NoticeDayTypeConverter;
import com.depromeet.breadmapbackend.global.converter.param.ReviewSortTypeConverter;
import com.depromeet.breadmapbackend.global.security.CurrentUserArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/docs/**")
			.addResourceLocations("classpath:/static/docs/")
			.setCachePeriod(20);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new CurrentUserArgumentResolver());
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new BakerySortTypeConverter());
		registry.addConverter(new ReviewSortTypeConverter());
		registry.addConverter(new NoticeDayTypeConverter());
		registry.addConverter(new AdminBakeryFilterConverter());
		registry.addConverter(new AdminBakeryImageTypeConverter());
		registry.addConverter(new FeedTypeConverter());
		registry.addConverter(new FeedStatusConverter());
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON) // 기본 미디어 유형을 설정합니다.
			.favorParameter(false) // 요청 파라미터를 통한 미디어 유형 지정을 사용하지 않도록 설정합니다.
			.mediaType("json", MediaType.APPLICATION_JSON)
			.useRegisteredExtensionsOnly(true); // 등록된 미디어 유형만 사용하도록 설정합니다.
	}
}
