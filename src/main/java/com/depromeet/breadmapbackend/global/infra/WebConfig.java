package com.depromeet.breadmapbackend.global.infra;

//import com.depromeet.breadmapbackend.domain.common.converter.AdminBakeryImageTypeConverter;
import com.depromeet.breadmapbackend.global.converter.BakerySortTypeConverter;
import com.depromeet.breadmapbackend.global.converter.NoticeDayTypeConverter;
import com.depromeet.breadmapbackend.global.converter.ReviewSortTypeConverter;
import com.depromeet.breadmapbackend.global.security.CurrentUserArgumentResolver;
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
//        registry.addConverter(new AdminBakeryImageTypeConverter());
    }
}
