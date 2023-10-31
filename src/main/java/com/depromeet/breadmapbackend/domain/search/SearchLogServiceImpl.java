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
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchLogServiceImpl implements SearchLogService {

    private final RedisTemplate redisTemplate;
    private static final long RECENT_KEYWORD_SIZE = 3;

    @Override
    public void saveRecentSearchLog(String oauthId, String keyword) {
        String now = LocalDateTime.now().toString();

        String key = searchLogKey(oauthId);
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
    public List<String> getRecentSearchLogs(String oauthId) {
        String key = searchLogKey(oauthId);
        List<SearchLog> range = redisTemplate.opsForList()
                .range(key, 0, RECENT_KEYWORD_SIZE);

        return Objects.requireNonNull(range).stream().map(SearchLog::getKeyword).toList();
    }

    @Override
    public void deleteRecentSearchLog(String oauthId, String keyword, String createdAt) {
        String key = searchLogKey(oauthId);
        SearchLog value = SearchLog.builder()
                .keyword(keyword)
                .createdAt(createdAt)
                .build();

        long count = redisTemplate.opsForList().remove(key, 1, value);

        if (count == 0) {
            throw new DaedongException(DaedongStatus.SEARCH_LOG_NOT_EXIST);
        }
    }

    private String searchLogKey(String oauthId) {
        return "searchLog:" + oauthId;
    }
}
