package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.domain.bakery.repository.ReviewRepositorySupport;
import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepositorySupport reviewRepositorySupport;

    @Transactional(readOnly = true)
    public List<SimpleReviewDto> getAllReviewList(){
        return reviewRepositorySupport.getAllReviewList();
    }
}
