package com.depromeet.breadmapbackend.domain.review.tag;

import com.depromeet.breadmapbackend.domain.review.tag.dto.TagListResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

}
