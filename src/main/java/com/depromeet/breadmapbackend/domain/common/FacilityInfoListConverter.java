package com.depromeet.breadmapbackend.domain.common;

import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class AmenityTypeListConverter implements AttributeConverter<List<FacilityInfo>, String> {
    
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<FacilityInfo> attribute) {
        if(attribute == null) return null;

        return String.join(SPLIT_CHAR,
                attribute.stream().
                        map(facilityInfo -> facilityInfo.name())
                        .collect(Collectors.toList()));
    }

    @Override
    public List<FacilityInfo> convertToEntityAttribute(String dbData) {
        if(dbData == null) return Collections.emptyList();

        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .map(code -> FacilityInfo.valueOf(code))
                .collect(Collectors.toList());
    }

}
