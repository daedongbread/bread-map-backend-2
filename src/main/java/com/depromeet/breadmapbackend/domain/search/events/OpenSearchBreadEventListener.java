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
public class OpenSearchBreadEventListener implements ApplicationListener<BreadCreationEvent> {

    private final OpenSearchService openSearchService;
    @Override
    public void onApplicationEvent(BreadCreationEvent event) {
        try {
            openSearchService.convertDataAndLoadToEngine(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion(), Collections.singletonList(event.getBreadLoadData()));
        } catch (IOException e) {
            log.error("====== OpenSearchBreadEvent Starting with " + event.getBreadLoadData().getBreadId() + " has been error");
        }
    }
}
