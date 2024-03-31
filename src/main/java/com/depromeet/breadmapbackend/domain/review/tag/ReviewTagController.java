package com.depromeet.breadmapbackend.domain.review.tag;

import com.depromeet.breadmapbackend.domain.challenge.dto.ChallengeSubmission;
import com.depromeet.breadmapbackend.domain.review.tag.dto.ReviewTagsRequest;
import com.depromeet.breadmapbackend.domain.review.tag.dto.TagListResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated(ValidationSequence.class)
@RestController
@RequestMapping("/v1/reviews/tags")
@RequiredArgsConstructor
public class ReviewTagController {

    private final ReviewTagService reviewTagService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TagListResponse> getAllReviewTags() {
        return new ApiResponse<>(reviewTagService.getAllReviewTags());
    }

    @PostMapping("/submission")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ChallengeSubmission> addFeed(
            @AuthenticationPrincipal CurrentUserInfo currentUserInfo,
            @RequestBody ReviewTagsRequest requestDto
    ) {
        //TODO:: 그렇지 않으면 이력 insert
        return null;
    }


}
