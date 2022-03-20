package com.depromeet.breadmapbackend.domain.common;

import com.depromeet.breadmapbackend.domain.bakery.AmenityType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class AmenityTypeListConverter implements AttributeConverter<List<AmenityType>, String> {
    
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<AmenityType> attribute) {
        if(attribute == null) return null;

        return String.join(SPLIT_CHAR,
                attribute.stream().
                        map(amenityType -> amenityType.name())
                        .collect(Collectors.toList()));
    }

    @Override
    public List<AmenityType> convertToEntityAttribute(String dbData) {
        if(dbData == null) return Collections.emptyList();

        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .map(code -> AmenityType.valueOf(code))
                .collect(Collectors.toList());
    }

}
