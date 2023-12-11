package com.depromeet.breadmapbackend.domain.admin.bakery.handler;

import com.depromeet.breadmapbackend.domain.search.dto.keyword.BakeryLoadData;
import com.depromeet.breadmapbackend.domain.search.events.BakeryCreationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenSearchEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishSaveBakery(final BakeryLoadData bakeryLoadData) {
        BakeryCreationEvent publishSaveBakery = new BakeryCreationEvent(this, bakeryLoadData);
        applicationEventPublisher.publishEvent(publishSaveBakery);
    }

}
