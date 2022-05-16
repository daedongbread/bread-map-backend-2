package com.depromeet.breadmapbackend.web.controller.review;

import com.depromeet.breadmapbackend.service.flag.FlagService;
import com.depromeet.breadmapbackend.service.review.ReviewService;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("all")
    public ApiResponse<List<SimpleReviewDto>> getAllReviewList(){
        return new ApiResponse<>(reviewService.getAllReviewList());
    }

}
