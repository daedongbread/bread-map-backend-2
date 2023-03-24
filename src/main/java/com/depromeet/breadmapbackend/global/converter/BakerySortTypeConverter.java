package com.depromeet.breadmapbackend.global.converter;

import com.depromeet.breadmapbackend.domain.bakery.BakerySortType;
import org.springframework.core.convert.converter.Converter;

public class BakerySortTypeConverter implements Converter<String, BakerySortType> {
    @Override
    public BakerySortType convert(String str) {
        return BakerySortType.valueOf(str.toUpperCase());
    }
}
