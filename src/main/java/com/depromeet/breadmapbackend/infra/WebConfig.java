package com.depromeet.breadmapbackend.infra;

import com.depromeet.breadmapbackend.domain.common.converter.BakerySortTypeConverter;
import com.depromeet.breadmapbackend.domain.common.converter.NoticeDayTypeConverter;
import com.depromeet.breadmapbackend.domain.common.converter.ReviewSortTypeConverter;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

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
    }
}
