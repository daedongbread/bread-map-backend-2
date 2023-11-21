package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OpenSearchDeleteBakeryEvent extends ApplicationEvent {
    OpenSearchIndex openSearchIndex;
    Long targetId;

    public OpenSearchDeleteBakeryEvent(Object source, OpenSearchIndex openSearchIndex, Long targetId) {
        super(source);
        this.openSearchIndex = openSearchIndex;
        this.targetId = targetId;
    }
}
