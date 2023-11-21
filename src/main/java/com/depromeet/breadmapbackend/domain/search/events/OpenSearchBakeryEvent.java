package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.keyword.BakeryLoadData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OpenSearchBakeryEvent extends ApplicationEvent {
    Long bakeryId;
    BakeryLoadData bakeryLoadData;

    public OpenSearchBakeryEvent(Object source, BakeryLoadData bakeryLoadData) {
        super(source);
        this.bakeryLoadData = bakeryLoadData;
    }
    public OpenSearchBakeryEvent(Object source, Long bakeryId) {
        super(source);
        this.bakeryId = bakeryId;
    }
}
