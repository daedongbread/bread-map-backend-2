package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.keyword.BreadLoadData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BreadCreationEvent extends ApplicationEvent {
    Long breadId;
    BreadLoadData breadLoadData;

    public BreadCreationEvent(Object source, BreadLoadData breadLoadData) {
        super(source);
        this.breadLoadData = breadLoadData;
    }
}
