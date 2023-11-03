package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.BakeryLoadData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OpenSearchDeleteEvent extends ApplicationEvent {
    OpenSearchIndex openSearchIndex;
    Long targetId;

    public OpenSearchDeleteEvent(Object source, OpenSearchIndex openSearchIndex, Long targetId) {
        super(source);
        this.openSearchIndex = openSearchIndex;
        this.targetId = targetId;
    }
}
