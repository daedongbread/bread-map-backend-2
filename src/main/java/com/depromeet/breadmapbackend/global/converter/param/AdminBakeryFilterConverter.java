package com.depromeet.breadmapbackend.global.converter.param;

import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryFilter;
import org.springframework.core.convert.converter.Converter;

public class AdminBakeryFilterConverter implements Converter<String, AdminBakeryFilter> {
    @Override
    public AdminBakeryFilter convert(String param) {
        return AdminBakeryFilter.findByCode(param);
    }
}
