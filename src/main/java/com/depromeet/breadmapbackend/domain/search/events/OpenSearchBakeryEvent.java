package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.keyword.BakeryLoadData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OpenSearchBakeryEvent extends ApplicationEvent {
    BakeryLoadData bakeryLoadData;

    public OpenSearchBakeryEvent(Object source, BakeryLoadData bakeryLoadData) {
        super(source);
        this.bakeryLoadData = bakeryLoadData;
    }
}
