package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenSearchLoadScheduler {

    private final OpenSearchService openSearchService;
//    @Scheduled(cron = "0 0 * * * *") // for test
    @Scheduled(cron = "0 0 1 * * *")
    public void loadDailyData() throws IOException {
        Config config = Config.fromYAML(new File(ResourceUtils.getFile("classpath:singleNodeConfig.yaml").toURI()));

        RedissonClient client = Redisson.create(config);

        RLock lock = client.getLock("Load-Daily-Data");
        try {

            if (lock.tryLock(1, TimeUnit.HOURS)) {
                log.info("========================= Loading daily data to search engine =========================");

                openSearchService.deleteIndex(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion());
                openSearchService.deleteIndex(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion());

                openSearchService.createIndex(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion());
                openSearchService.createIndex(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion());

                openSearchService.loadData();
                System.out.println("Job loadDailyData executed by this instance");
            } else {
                System.out.println("Job loadDailyData skipped by this instance");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

    }

//    @Scheduled(cron = "0/30 * * * * *") // for test
    @Scheduled(cron = "0 0 * * * *")
    public void loadHourlyData() throws IOException {
        Config config = Config.fromYAML(new File(ResourceUtils.getFile("classpath:singleNodeConfig.yaml").toURI()));

        RedissonClient client = Redisson.create(config);

        RLock dailyLock = client.getLock("Load-Daily-Data");
        RLock hourlyLock = client.getLock("Load-Hourly-Data");
        try {

            if (!dailyLock.isLocked() && hourlyLock.tryLock(1, TimeUnit.HOURS)) {
                log.info("========================= Loading hourly data to search engine =========================");

                openSearchService.loadData();
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
