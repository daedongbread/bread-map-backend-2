package com.depromeet.breadmapbackend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum DaedongStatus {
    // 400 BAD REQUEST
    BLOCK_MYSELF(BAD_REQUEST, 40012, "cannot block myself"), // 나를 차단/차단 해제할 때
    BAKERY_SORT_TYPE_EXCEPTION(BAD_REQUEST, 40020, "invalid bakery sort type"), // 빵집 정렬 조건이 틀렸을 때
    PRODUCT_ADD_REPORT_IMAGE_NOT_MAIN_EXCEPTION(BAD_REQUEST, 40030, "product add report image is not main"), // 상품 추가 제보 이미지가 메인이 아닐 때
    FLAG_COLOR_EXCEPTION(BAD_REQUEST, 40040, "invalid flag color"), // 올바르지 않은 깃발 색깔일 때
    FLAG_UNEDIT_EXCEPTION(BAD_REQUEST, 40041,"unedit flag"), // 수정 또는 삭제할 수 없는 리스트(기본 리스트 : 가봤어요, 가고싶어요)일 때
    REVIEW_SORT_TYPE_EXCEPTION(BAD_REQUEST, 40050, "invalid review sort type"), // 리뷰 정렬 조건이 틀렸을 때
    REVIEW_PAGE_EXCEPTION(BAD_REQUEST, 40050, "invalid review page request"), // 리뷰 페이지 조건이 틀렸을 때
    REVIEW_COMMENT_UNDELETE_EXCEPTION(BAD_REQUEST, 40050, "review comment undelete"), // 리뷰 댓글을 지울 수 없을 때
    NOTICE_TYPE_EXCEPTION(BAD_REQUEST, 40060, "invalid notice type"), // 알림 타입이 틀렸을 때
    NOTICE_DAY_TYPE_EXCEPTION(BAD_REQUEST, 40061, "invalid notice day type"), // 알림 날짜 조건이 틀렸을 때
    NOTICE_PAGE_EXCEPTION(BAD_REQUEST, 40062, "invalid notice page request"), // 알림 페이지 조건이 틀렸을 때
    IMAGE_INVALID_EXCEPTION(BAD_REQUEST, 40080, "image is invalid"), // 이미지가 유효하지 않을 때
    IMAGE_NUM_UNMATCH_EXCEPTION(BAD_REQUEST, 40081, "image number unmatch with product number"), // 이미지 개수가 상품 개수와 일치하지 않을 때
    IMAGE_NUM_EXCEED_EXCEPTION(BAD_REQUEST, 40082, "image number exceed"), // 이미지 개수가 초과될 때
    ADMIN_KEY_EXCEPTION(BAD_REQUEST, 40090, "invalid admin key"), // 관리자 회원가입에서 키가 틀렸을 때
    ADMIN_PAGE_EXCEPTION(BAD_REQUEST, 40091, "invalid admin page request"), // 관리자 관련 페이지 조건이 틀렸을 때
    ADMIN_IMAGE_TYPE_EXCEPTION(BAD_REQUEST, 40092, "invalid admin image type"), // 관리자 관련 이미지 조건이 틀렸을 때  TODO
    ADMIN_IMAGE_UNDELETE_EXCEPTION(BAD_REQUEST, 40093, "admin image undelete"), // 관리자 관련 이미지가 사용 중이서 삭제할 수 없을 때

    // 401 UNAUTHORIZED
    CUSTOM_AUTHENTICATION_ENTRYPOINT(UNAUTHORIZED, 40100, "invalid jwt"), // 전달한 Jwt 이 정상적이지 않은 경우 발생 시키는 예외
    TOKEN_INVALID_EXCEPTION(UNAUTHORIZED, 40101, "invalid token"), // access or refresh token이 유효하지 않을 때

    // 403 FORBIDDEN
    CUSTOM_ACCESS_DENIED(FORBIDDEN, 40300, "access denied"), // 권한이 없는 리소스를 요청한 경우 발생 시키는 예외
    BLOCK_USER(FORBIDDEN, 40310, "blocked user"), // 차단된 유저일 떄
    REJOIN_RESTRICT(FORBIDDEN, 40311, "rejoin is not possible within 7 days of withdrawal"), // 탈퇴한지 7일이 지나지 않았는데 재가입 시

    // 404 NOT FOUND
    USER_NOT_FOUND(NOT_FOUND, 40410, "user not found"), // 유저가 존재하지 않을 때
    FOLLOW_NOT_FOUND(NOT_FOUND, 40411, "follow not found"), // 팔로우가 되어 있지 않을 때
    BLOCK_NOT_FOUND(NOT_FOUND, 40412, "block not found"), // 차단하지 않은 유저일 때
    BAKERY_NOT_FOUND(NOT_FOUND, 40420, "bakery not found"), // 빵집이 존재하지 않을 때
    BAKERY_VIEW_NOT_FOUND(NOT_FOUND, 40421, "bakery view not found"), // 상품이 존재하지 않을 때
    PRODUCT_NOT_FOUND(NOT_FOUND, 40430, "product not found"), // 상품이 존재하지 않을 때
    FLAG_NOT_FOUND(NOT_FOUND, 40440, "flag not found"), // 존재하지 않은 리스트일 때
    FLAG_BAKERY_NOT_FOUND(NOT_FOUND, 40041, "bakery not found in flag"), // 해당 리스트에 없는 빵집일 때
    REVIEW_NOT_FOUND(NOT_FOUND, 40450, "review not found"), // 존재하지 않은 리뷰일 때
    REVIEW_COMMENT_NOT_FOUND(NOT_FOUND, 40451, "review comment not found"), // 존재하지 않은 리뷰일 때
    REVIEW_IMAGE_NOT_FOUND(NOT_FOUND, 40452, "review image not found"), // 존재하지 않은 리뷰 이미지일 때
//    NOTICE_TOKEN_NOT_FOUND(NOT_FOUND, 40460, "notice token not found"), // 알림 토큰이 존재하지 않을 때
    ADMIN_NOT_FOUND(NOT_FOUND, 40490, "admin not found"), // 관리자가 존재하지 않을 때
    BAKERY_REPORT_NOT_FOUND(NOT_FOUND, 40491, "bakery report not found"), // 빵집 제보가 존재하지 않을 때
    BAKERY_IMAGE_REPORT_NOT_FOUND(NOT_FOUND, 40491, "bakery image report not found"), // 빵집 이미지 제보가 존재하지 않을 때
    REVIEW_REPORT_NOT_FOUND(NOT_FOUND, 40492, "review report not found"), // 존재하지 않는 리뷰 신고일 때
    PRODUCT_ADD_REPORT_NOT_FOUND(NOT_FOUND, 40493, "product add report not found"), // 상품 추가 제보가 존재하지 않을 때
    PRODUCT_ADD_REPORT_IMAGE_NOT_FOUND(NOT_FOUND, 40494, "product add report image not found"), // 상품 추가 제보 이미지가 존재하지 않을 때

    // 409 CONFLICT
    NICKNAME_DUPLICATE_EXCEPTION(CONFLICT, 40910, "nickname already exists"), // 유저 닉네임이 이미 존재할 때
    FOLLOW_DUPLICATE_EXCEPTION(CONFLICT, 40911, "already follow user"), // 이미 팔로우가 되어 있을 때
    SELF_FOLLOW_EXCEPTION(BAD_REQUEST, 40912, "self follow or unfollow exception"), // 본인을 팔로우/언팔로우 했을 때
    BLOCK_DUPLICATE_EXCEPTION(CONFLICT, 40913, "already block user"), // 이미 차단한 유저일 때
    PRODUCT_DUPLICATE_EXCEPTION(CONFLICT, 40930, "product duplicate"), // 이미 존재하는 상품일 때
    FLAG_DUPLICATE_EXCEPTION(CONFLICT, 40940, "flag duplicate"), // 이미 존재하는 리스트일 때
    FLAG_BAKERY_DUPLICATE_EXCEPTION(CONFLICT, 40941, "bakery already in this flag"), // 이미 해당 리스트에 등록된 빵집일 때
    REVIEW_LIKE_DUPLICATE_EXCEPTION(CONFLICT, 40950, "already like review"), // 이미 좋아요를 누른 리뷰일 때
    REVIEW_UNLIKE_DUPLICATE_EXCEPTION(CONFLICT, 40951, "already unlike review"), // 이미 좋아요를 취소한 리뷰일 때
    REVIEW_COMMENT_LIKE_DUPLICATE_EXCEPTION(CONFLICT, 40952, "already lik review comment"), // 이미 좋아요를 누른 댓글일 때
    REVIEW_COMMENT_UNLIKE_DUPLICATE_EXCEPTION(CONFLICT, 40953, "already unlike review comment"), // 이미 좋아요를 취소한 댓글일 때
//    NOTICE_TOKEN_DUPLICATE_EXCEPTION(CONFLICT, 40960, "notice token duplicate"), // 알림 토큰이 이미 존재할 때
    ADMIN_EMAIL_DUPLICATE_EXCEPTION(CONFLICT, 40990, "admin email duplicate") // 관리자 이메일 중복일 때
    ;

    // 500

    private final HttpStatus status;
    private final Integer code;
    private final String description;
}
