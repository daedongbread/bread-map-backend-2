package com.depromeet.breadmapbackend.domain.common;

import javax.persistence.AttributeConverter;
import java.util.*;
import java.util.stream.Collectors;

public class LongListConverter implements AttributeConverter<Set<Long>, String> {
    @Override
    public String convertToDatabaseColumn(Set<Long> attribute) {
        return attribute == null ? null : attribute.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    @Override
    public Set<Long> convertToEntityAttribute(String dbData) {
        return dbData.isBlank() ? new LinkedHashSet<>(Collections.emptyList()) : new LinkedHashSet<>(Arrays.stream(Arrays.stream(dbData.split(",")).mapToLong(Long::parseLong).toArray()).boxed().collect(Collectors.toList()));
    }
}
