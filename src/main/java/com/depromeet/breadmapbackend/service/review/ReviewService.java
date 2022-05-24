package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {

    List<ReviewDTO> getAllReviewList();

    List<ReviewDTO> getBakeryReviewList(long bakeryId);

    Object addReview(long userId, long bakeryId, String breadId, String content, String rating);
}
