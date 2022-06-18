package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.domain.review.repository.ReviewRepositorySupport;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepositorySupport reviewRepositorySupport;

    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviewList(){
        return reviewRepositorySupport.getAllReviewList();
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getBakeryReviewList(long bakeryId){
        return reviewRepositorySupport.getBakeryReviewList(bakeryId);
    }

    @Transactional()
    public Object addReview(Map<String, Object> data){
        return reviewRepositorySupport.addReview(data);
    }
}
