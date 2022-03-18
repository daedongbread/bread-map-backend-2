package com.depromeet.breadmapbackend.controller.review;

import com.depromeet.breadmapbackend.service.flag.FlagService;
import com.depromeet.breadmapbackend.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
}
