package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BakeryDeletionEvent extends ApplicationEvent {
    OpenSearchIndex openSearchIndex;
    Long bakeryId;

    public BakeryDeletionEvent(Object source, OpenSearchIndex openSearchIndex, Long bakeryId) {
        super(source);
        this.openSearchIndex = openSearchIndex;
        this.bakeryId = bakeryId;
    }
}
