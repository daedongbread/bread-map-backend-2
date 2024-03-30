package com.depromeet.breadmapbackend.domain.challenge;

import com.depromeet.breadmapbackend.domain.challenge.dto.ChallengeSubmission;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated(ValidationSequence.class)
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/challenge")
public class VerificationController {

    private final SubmissionService submissionService;

    @PostMapping("/submission")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ChallengeSubmission> addFeed(
            @AuthenticationPrincipal CurrentUserInfo currentUserInfo,
            @RequestBody ChallengeSubmission requestDto
    ) {
        if(submissionService.checkChallengeSubmissionToday(currentUserInfo.getId(), requestDto.getChallengeId())) {
            throw new DaedongException(DaedongStatus.CHALLENGE_SUBMISSION_DUPLICATE_EXCEPTION);
        }
        //TODO:: 그렇지 않으면 이력 insert
        return null;
    }

}
