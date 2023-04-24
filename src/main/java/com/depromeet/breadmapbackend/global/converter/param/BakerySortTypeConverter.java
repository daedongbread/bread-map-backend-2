package com.depromeet.breadmapbackend.global.converter.param;

import com.depromeet.breadmapbackend.domain.bakery.BakerySortType;
import org.springframework.core.convert.converter.Converter;

public class BakerySortTypeConverter implements Converter<String, BakerySortType> {
    @Override
    public BakerySortType convert(String param) {
        return BakerySortType.findByCode(param);
    }
}
