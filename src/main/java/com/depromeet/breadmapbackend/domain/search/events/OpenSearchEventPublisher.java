package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.dto.keyword.BakeryLoadData;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.BreadLoadData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenSearchEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishSaveBakery(final BakeryLoadData bakeryLoadData) {
        OpenSearchBakeryEvent publishSaveBakery = new OpenSearchBakeryEvent(this, bakeryLoadData);
        applicationEventPublisher.publishEvent(publishSaveBakery);
    }

    public void publishSaveBread(final BreadLoadData breadLoadData) {
        OpenSearchBreadEvent publishSaveBread = new OpenSearchBreadEvent(this, breadLoadData);
        applicationEventPublisher.publishEvent(publishSaveBread);
    }

    public void publishDeleteBread(final Long breadId) {
        OpenSearchBreadEvent publishDeleteBread = new OpenSearchBreadEvent(this, breadId);
        applicationEventPublisher.publishEvent(publishDeleteBread);
    }
}
