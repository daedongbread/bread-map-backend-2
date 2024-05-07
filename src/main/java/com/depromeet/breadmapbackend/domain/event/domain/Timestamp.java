package com.depromeet.breadmapbackend.domain.event.domain;

import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class Timestamp {
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
