package com.depromeet.breadmapbackend.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum DaedongStatus {
    // 400 BAD REQUEST

    // 401 UNAUTHORIZED

    // 403 FORBIDDEN

    // 404 NOT FOUND
    USER_NOT_FOUND(NOT_FOUND, 40010, "user not found"), // 유저가 존재하지 않을 때
    FOLLOW_NOT_FOUND(NOT_FOUND, 40011, "follow not found"), // 팔로우가 되어 있지 않을 때
    BLOCK_NOT_FOUND(NOT_FOUND, 40012, "block not found"), // 차단하지 않은 유저일 때
    BAKERY_NOT_FOUND(NOT_FOUND, 40020, "bakery not found"), // 빵집이 존재하지 않을 때
    PRODUCT_NOT_FOUND(NOT_FOUND, 40030, "product not found"), // 상품이 존재하지 않을 때
    FLAG_NOT_FOUND(NOT_FOUND, 40040, "flag not found"), // 존재하지 않은 리스트일 때
    FLAG_BAKERY_NOT_FOUND(NOT_FOUND, 40041, "bakery not found in flag"), // 해당 리스트에 없는 빵집일 때
    REVIEW_NOT_FOUND(NOT_FOUND, 40050, "review not found"), // 존재하지 않은 리뷰일 때
    REVIEW_COMMENT_NOT_FOUND(NOT_FOUND, 40051, "review comment not found"), // 존재하지 않은 리뷰일 때
    NOTICE_TOKEN_NOT_FOUND(NOT_FOUND, 40060, "notice token not found"), // 알림 토큰이 존재하지 않을 때
    ADMIN_NOT_FOUND(NOT_FOUND, 40090, "admin not found"), // 관리자가 존재하지 않을 때
    BAKERY_REPORT_NOT_FOUND(NOT_FOUND, 40091, "bakery report not found"), // 빵집 제보가 존재하지 않을 때
    REVIEW_REPORT_NOT_FOUND(NOT_FOUND, 40092, "review report not found"), // 존재하지 않는 리뷰 신고일 때
    ;

    // 409 CONFLICT

    // 500

    private HttpStatus status;
    private final Integer code;
    private final String description;
}
