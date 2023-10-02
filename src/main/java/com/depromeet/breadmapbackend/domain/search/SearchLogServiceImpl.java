package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.SearchLog;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchLogServiceImpl implements SearchLogService {

    private final RedisTemplate redisTemplate;
    private static final long RECENT_KEYWORD_SIZE = 3;

    @Override
    public void saveRecentSearchLog(Long id, String keyword) {
        String now = LocalDateTime.now().toString();

        String key = searchLogKey(id);
        SearchLog value = SearchLog.builder().
                keyword(keyword).
                createdAt(now).
                build();

        Long size = redisTemplate.opsForList().size(key);
        if (size == RECENT_KEYWORD_SIZE) {
            redisTemplate.opsForList().rightPop(key);
        }

        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public List<SearchLog> getRecentSearchLogs(Long id) {
        String key = searchLogKey(id);
        return redisTemplate.opsForList()
                        .range(key, 0, RECENT_KEYWORD_SIZE);
    }

    @Override
    public void deleteRecentSearchLog(Long id, String keyword, String createdAt) {
        String key = searchLogKey(id);
        SearchLog value = SearchLog.builder()
                .keyword(keyword)
                .createdAt(createdAt)
                .build();

        long count = redisTemplate.opsForList().remove(key, 1, value);

        if (count == 0) {
            throw new DaedongException(DaedongStatus.SEARCH_LOG_NOT_EXIST);
        }
    }

    private String searchLogKey(Long userId) {
        return "searchLog:" + userId;
    }
}
