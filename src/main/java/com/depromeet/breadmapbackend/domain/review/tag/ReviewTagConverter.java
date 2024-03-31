package com.depromeet.breadmapbackend.domain.review.tag;

import javax.persistence.AttributeConverter;
import java.util.List;

public class ReviewTagConverter implements AttributeConverter<List<BakeryTag>, Long> {
    @Override
    public Long convertToDatabaseColumn(List<BakeryTag> attribute) {
        return null;
    }

    @Override
    public List<BakeryTag> convertToEntityAttribute(Long dbData) {
        return null;
    }
}
