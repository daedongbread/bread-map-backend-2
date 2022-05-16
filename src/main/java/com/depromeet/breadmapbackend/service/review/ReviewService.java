package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;

import java.util.List;

public interface ReviewService {

    List<SimpleReviewDto> getAllReviewList();
}
