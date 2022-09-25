package com.depromeet.breadmapbackend.domain.common.converter;

import com.depromeet.breadmapbackend.domain.review.ReviewSortType;
import org.springframework.core.convert.converter.Converter;

public class ReviewSortTypeConverter implements Converter<String, ReviewSortType> {
    @Override
    public ReviewSortType convert(String str) {
        return ReviewSortType.valueOf(str.toUpperCase());
    }
}