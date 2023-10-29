package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenSearchLoadScheduler {

    private final OpenSearchService openSearchService;
//    @Scheduled(cron = "0 0/5 * * * *") // for test
    @Scheduled(cron = "0 0 0 1,15 * *")
    public void loadEntireData() throws IOException {
        RedissonClient client = Redisson.create();

        RLock lock = client.getLock("Load-Entire-Data");
        try {

            if (lock.tryLock(2, TimeUnit.HOURS)) {
                log.info("========================= Loading entire data to search engine =========================");

                openSearchService.deleteAndCreateIndex(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion());
                openSearchService.deleteAndCreateIndex(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion());

                openSearchService.loadEntireData();
                System.out.println("Job loadEntireData executed by this instance");
            } else {
                System.out.println("Job loadEntireData skipped by this instance");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

    }

//    @Scheduled(cron = "0 0/30 * * * *") // for test
    @Scheduled(cron = "0 0 * * * *")
    public void loadHourlyData() throws IOException {
        RedissonClient client = Redisson.create();

        RLock dailyLock = client.getLock("Load-Entire-Data");
        RLock hourlyLock = client.getLock("Load-Hourly-Data");
        try {

            if (!dailyLock.isLocked() && hourlyLock.tryLock(1, TimeUnit.HOURS)) {
                log.info("========================= Loading hourly data to search engine =========================");

                openSearchService.loadHourlyData();
                System.out.println("Job loadHourlyData executed by this instance");
            } else {
                System.out.println("Job loadHourlyData skipped by this instance");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            hourlyLock.unlock();
        }

    }
}
