package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;

import java.util.List;

public interface ReviewService {

    List<SimpleReviewDto> getAllReviewList();

    String addReview(long userId, long bakeryId, long breadId, String content, Integer rating);
}
