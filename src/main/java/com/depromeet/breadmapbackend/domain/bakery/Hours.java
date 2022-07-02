package com.depromeet.breadmapbackend.domain.bakery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Hours {
    private String monOpen;
    private String monClose;
    private String tueOpen;
    private String tueClose;
    private String wedOpen;
    private String wedClose;
    private String thuOpen;
    private String thuClose;
    private String friOpen;
    private String friClose;
    private String satOpen;
    private String satClose;
    private String sunOpen;
    private String sunClose;
}