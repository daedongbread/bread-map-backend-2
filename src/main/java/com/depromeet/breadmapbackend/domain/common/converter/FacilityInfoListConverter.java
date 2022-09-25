package com.depromeet.breadmapbackend.domain.common.converter;

import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FacilityInfoListConverter implements AttributeConverter<List<FacilityInfo>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<FacilityInfo> attribute) {
        return attribute == null ? null : attribute.stream().map(Enum::name).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<FacilityInfo> convertToEntityAttribute(String dbData) {
        return (dbData == null || dbData.isBlank()) ? Collections.emptyList() : Arrays.stream(dbData.split(SPLIT_CHAR)).map(FacilityInfo::valueOf).collect(Collectors.toList());
    }
}
