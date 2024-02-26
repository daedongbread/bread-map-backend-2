package com.depromeet.breadmapbackend.domain.challenge.breaddiary;

/**
 * 빵일기 쓰기 검수 상태
 */
public enum BreadDiaryStatus {
    /**
     * 미확인
     */
    UNCHECKED,
    /**
     * 승인
     */
    APPROVED,
    /**
     * 반려
     */
    REJECTED
}
