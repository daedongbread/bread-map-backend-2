package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class OpenSearchLoadProdScheduler {

    private final OpenSearchService openSearchService;
    @Scheduled(cron = "0 0 0 1,15 * *")
    public void loadEntireData() throws IOException {
        RedissonClient client = Redisson.create();

        RLock lock = client.getLock("Load-Entire-Data");
        try {

            if (lock.tryLock(1, TimeUnit.HOURS)) {
                log.info("========================= Loading entire data to search engine =========================");

                openSearchService.deleteAndCreateIndex(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion());
                openSearchService.deleteAndCreateIndex(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion());

                openSearchService.loadEntireData();
                log.info("Job loadEntireData executed by this instance");
            } else {
                log.info("Job loadEntireData skipped by this instance");
            }
        } catch (InterruptedException e) {
            log.error("Job loadEntireData error" + e.getMessage());
            lock.unlock();
            Thread.currentThread().interrupt();
        } finally {
            log.info("Job loadEntireData reached finally");
            lock.unlock();
        }

    }
}
