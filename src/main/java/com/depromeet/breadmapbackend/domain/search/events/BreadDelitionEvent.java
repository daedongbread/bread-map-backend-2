package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BreadDelitionEvent extends ApplicationEvent {
    OpenSearchIndex openSearchIndex;
    Long breadId;

    public BreadDelitionEvent(Object source, OpenSearchIndex openSearchIndex, Long breadId) {
        super(source);
        this.openSearchIndex = openSearchIndex;
        this.breadId = breadId;
    }
}
