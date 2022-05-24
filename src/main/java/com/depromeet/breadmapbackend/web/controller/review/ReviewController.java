package com.depromeet.breadmapbackend.web.controller.review;

import com.depromeet.breadmapbackend.service.review.ReviewService;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Created by ParkSuHo by 2022/03/18.
 * Updated by ChoiHyunWoo by 2022/05/16
 */
@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("")
    public ApiResponse<List<ReviewDTO>> getBakeryReviewList(@RequestParam long bakeryId){
        return new ApiResponse<>(reviewService.getBakeryReviewList(bakeryId));
    }

    @GetMapping("all")
    public ApiResponse<List<ReviewDTO>> getAllReviewList(){
        return new ApiResponse<>(reviewService.getAllReviewList());
    }

    @PostMapping("add")
    public ApiResponse<Object> addReview(
            @RequestParam long userId, @RequestParam long bakeryId,
            @RequestParam String breadId, @RequestParam String content,
            @RequestParam String rating ){
        return new ApiResponse<>(reviewService.addReview(userId, bakeryId, breadId, content, rating));
    }
}
