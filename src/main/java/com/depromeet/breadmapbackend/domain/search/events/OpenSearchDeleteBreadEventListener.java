package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.OpenSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class OpenSearchDeleteBreadEventListener implements ApplicationListener<OpenSearchDeleteBakeryEvent> {
    private final OpenSearchService openSearchService;

    @Override
    public void onApplicationEvent(OpenSearchDeleteBakeryEvent event) {
        try {
            openSearchService.deleteIndex(event.getOpenSearchIndex(), event.targetId);
        } catch (IOException e) {
            log.error("====== onApplicationEvent Starting with " + event.getOpenSearchIndex() + event.getTargetId() + " has been error");
        }
    }
}
