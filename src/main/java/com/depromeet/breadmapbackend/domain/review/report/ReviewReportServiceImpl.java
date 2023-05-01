package com.depromeet.breadmapbackend.domain.review.report;

import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.review.report.dto.ReviewReportRequest;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewReportServiceImpl implements ReviewReportService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewReportRepository reviewReportRepository;

    @Transactional(rollbackFor = Exception.class)
    public void reviewReport(String oAuthId, Long reviewId, ReviewReportRequest request) {
        User reporter = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findByIdAndIsBlockIsFalseAndIsDeleteIsFalse(reviewId).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        if (request.getReason().equals(ReviewReportReason.ETC) && request.getContent().length() < 10)
            throw new DaedongException(DaedongStatus.REVIEW_REPORT_CONTENT_EXCEPTION);

        ReviewReport reviewReport = ReviewReport.builder()
                .reporter(reporter).review(review).reason(request.getReason()).content(request.getContent()).build();

        reviewReportRepository.save(reviewReport);
    }
}
