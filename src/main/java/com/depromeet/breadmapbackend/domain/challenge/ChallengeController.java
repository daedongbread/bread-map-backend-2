package com.depromeet.breadmapbackend.domain.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/challenges")
@RestController
public class ChallengeController {

    private final ChallengeService challengeService;


}
