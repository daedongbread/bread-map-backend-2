package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.keyword.BakeryLoadData;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.BreadLoadData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OpenSearchBreadEvent extends ApplicationEvent {
    Long breadId;
    BreadLoadData breadLoadData;

    public OpenSearchBreadEvent(Object source, BreadLoadData breadLoadData) {
        super(source);
        this.breadLoadData = breadLoadData;
    }
}
