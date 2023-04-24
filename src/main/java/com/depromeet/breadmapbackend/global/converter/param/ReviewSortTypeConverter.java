package com.depromeet.breadmapbackend.global.converter.param;

import com.depromeet.breadmapbackend.domain.review.ReviewSortType;
import org.springframework.core.convert.converter.Converter;

public class ReviewSortTypeConverter implements Converter<String, ReviewSortType> {
    @Override
    public ReviewSortType convert(String param) {
        return ReviewSortType.findByCode(param);
    }
}