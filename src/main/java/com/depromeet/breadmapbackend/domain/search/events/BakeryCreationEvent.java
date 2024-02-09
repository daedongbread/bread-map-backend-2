package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.keyword.BakeryLoadData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BakeryCreationEvent extends ApplicationEvent {
    Long bakeryId;
    BakeryLoadData bakeryLoadData;

    public BakeryCreationEvent(Object source, BakeryLoadData bakeryLoadData) {
        super(source);
        this.bakeryLoadData = bakeryLoadData;
    }
}
