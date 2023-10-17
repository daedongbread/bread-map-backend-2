package com.depromeet.breadmapbackend.domain.subway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubwayJsonStringValidator {
    private ObjectMapper objectMapper;

    public SubwayJsonStringValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<SubwayStation> jsonStringParse(String jsonSubwayAsString) throws JsonProcessingException {
        List<SubwayStation> subwayStationList = new ArrayList<>();

        subwayStationList = Arrays.asList(objectMapper.readValue(jsonSubwayAsString, SubwayStation[].class));

        List<SubwayStation> participantJsonList = objectMapper.readValue(jsonSubwayAsString, new TypeReference<List<SubwayStation>>(){});

        return participantJsonList;
    }
}
