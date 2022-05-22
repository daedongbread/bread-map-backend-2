package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;

import java.util.List;

public interface ReviewService {

    List<SimpleReviewDto> getAllReviewList();

    Object addReview(long userId, long bakeryId, String breadId, String content, String rating);
}
