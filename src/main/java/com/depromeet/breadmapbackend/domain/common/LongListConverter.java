package com.depromeet.breadmapbackend.domain.common;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LongListConverter implements AttributeConverter<List<Long>, String> {
    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        return attribute == null ? null : attribute.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        return dbData == null ? Collections.emptyList() : new ArrayList<Long>(Arrays.stream(Arrays.stream(dbData.split(",")).mapToLong(Long::parseLong).toArray()).boxed().collect(Collectors.toList()));
    }
}
