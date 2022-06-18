package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewDTO;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    List<ReviewDTO> getAllReviewList();

    List<ReviewDTO> getBakeryReviewList(long bakeryId);

    Object addReview(Map<String, Object> data);
}
