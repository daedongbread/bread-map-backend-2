package com.depromeet.breadmapbackend.domain.challenge;

import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/challenges")
@RestController
public class ChallengeController {

    private final ChallengeService challengeService;

    /**
     * 챌린지 참가
     */
    @PostMapping("/{challengeId}/participants")
    public void participateChallenge(@CurrentUser String oAuthId, @PathVariable long challengeId) {
        challengeService.participate(oAuthId, challengeId);
    }

}
