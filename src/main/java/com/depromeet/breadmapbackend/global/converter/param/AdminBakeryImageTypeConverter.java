package com.depromeet.breadmapbackend.global.converter.param;

import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryImageType;
import org.springframework.core.convert.converter.Converter;

public class AdminBakeryImageTypeConverter implements Converter<String, AdminBakeryImageType> {
    @Override
    public AdminBakeryImageType convert(String param) {
        return AdminBakeryImageType.findByCode(param);
    }
}
