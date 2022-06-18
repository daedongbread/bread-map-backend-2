package com.depromeet.breadmapbackend.web.controller.review;

import com.depromeet.breadmapbackend.service.review.ReviewService;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 * Created by ParkSuHo by 2022/03/18.
 * Updated by ChoiHyunWoo by 2022/06/11
 */
@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public ApiResponse<List<ReviewDTO>> getBakeryReviewList(@PathVariable("id") long bakeryId){
        return new ApiResponse<>(reviewService.getBakeryReviewList(bakeryId));
    }

    @GetMapping("all")
    public ApiResponse<List<ReviewDTO>> getAllReviewList(){
        return new ApiResponse<>(reviewService.getAllReviewList());
    }

    @PostMapping("add")
    public ApiResponse<Object> addReview(@RequestBody Map<String, Object> data){
        return new ApiResponse<>(reviewService.addReview(data));
    }
}
