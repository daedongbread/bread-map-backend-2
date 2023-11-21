package com.depromeet.breadmapbackend.domain.search.events;

import com.depromeet.breadmapbackend.domain.search.OpenSearchService;
import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class OpenSearchBakeryEventListener implements ApplicationListener<OpenSearchBakeryEvent> {
    private final OpenSearchService openSearchService;
    @Override
    public void onApplicationEvent(OpenSearchBakeryEvent event) {
        try {
            openSearchService.convertDataAndLoadToEngine(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion(), Collections.singletonList(event.getBakeryLoadData()));
        } catch (IOException e) {
            log.error("====== OpenSearchBakeryEvent Starting with " + event.getBakeryLoadData().getBakeryId() + " has been error");
        }
    }
}
