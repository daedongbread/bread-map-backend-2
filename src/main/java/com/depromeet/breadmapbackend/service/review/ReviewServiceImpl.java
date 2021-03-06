package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.exception.BreadNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.exception.SortTypeWrongException;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.BreadRepository;
import com.depromeet.breadmapbackend.domain.common.FileConverter;
import com.depromeet.breadmapbackend.domain.common.ImageFolderPath;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.review.exception.*;
import com.depromeet.breadmapbackend.domain.review.repository.*;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.service.S3Uploader;
import com.depromeet.breadmapbackend.web.controller.review.dto.*;
import com.depromeet.breadmapbackend.web.controller.user.dto.UserReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BakeryRepository bakeryRepository;
    private final BreadRepository breadRepository;
    private final BreadRatingRepository breadRatingRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewCommentLikeRepository reviewCommentLikeRepository;
    private final FollowRepository followRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public List<ReviewDto> getBakeryReviewList(Long bakeryId, ReviewSortType sort){
        Comparator<ReviewDto> comparing;
        if(sort.equals(ReviewSortType.latest)) comparing = Comparator.comparing(ReviewDto::getId).reversed();
        else if(sort.equals(ReviewSortType.high)) comparing = Comparator.comparing(ReviewDto::getAverageRating).reversed();
        else if(sort.equals(ReviewSortType.low)) comparing = Comparator.comparing(ReviewDto::getAverageRating);
        else throw new SortTypeWrongException();

        return reviewRepository.findByBakeryId(bakeryId)
                .stream().filter(Review::isUse).map(br -> new ReviewDto(br,
//                            Math.toIntExact(reviewRepository.countByUserId(br.getUser().getId())),
                        reviewRepository.countByUser(br.getUser()),
                        followRepository.countByFromUser(br.getUser())
                ))
                .sorted(comparing)
                .limit(3)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getAllBakeryReviewList(Long bakeryId, ReviewSortType sort){
        Comparator<ReviewDto> comparing;
        if(sort.equals(ReviewSortType.latest)) comparing = Comparator.comparing(ReviewDto::getId).reversed();
        else if(sort.equals(ReviewSortType.high)) comparing = Comparator.comparing(ReviewDto::getAverageRating).reversed();
        else if(sort.equals(ReviewSortType.low)) comparing = Comparator.comparing(ReviewDto::getAverageRating);
        else throw new SortTypeWrongException();

        return reviewRepository.findByBakeryId(bakeryId)
                .stream().filter(Review::isUse).map(br -> new ReviewDto(br,
//                            Math.toIntExact(reviewRepository.countByUserId(br.getUser().getId()))
                        reviewRepository.countByUser(br.getUser()),
                        followRepository.countByFromUser(br.getUser())
                ))
                .sorted(comparing)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewDetailDto getReview(Long reviewId) {
        Review review = reviewRepository.findByIdAndIsUseIsTrue(reviewId).orElseThrow(ReviewNotFoundException::new);

        List<SimpleReviewDto> userOtherReviews = reviewRepository.findByUserId(review.getUser().getId()).stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(5).map(SimpleReviewDto::new).collect(Collectors.toList());

        List<SimpleReviewDto> bakeryOtherReviews = reviewRepository.findByBakeryId(review.getBakery().getId()).stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(5).map(SimpleReviewDto::new).collect(Collectors.toList());

        return ReviewDetailDto.builder()
                .review(review)
//                .reviewNum(Math.toIntExact(reviewRepository.countByUserId(review.getUser().getId())))
                .reviewNum(reviewRepository.countByUser(review.getUser()))
                .followerNum(followRepository.countByToUser(review.getUser()))
                .userOtherReviews(userOtherReviews)
                .bakeryOtherReviews(bakeryOtherReviews)
                .build();
    }

    @Transactional
    public void addReview(String username, Long bakeryId, ReviewRequest request, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);

        Review review = Review.builder()
                .user(user).bakery(bakery).content(request.getContent()).isUse(true).build();
        for (MultipartFile file : files) {
            String imagePath = fileConverter.parseFileInfo(file, ImageFolderPath.reviewAddImage, bakeryId);
            String image = s3Uploader.upload(file, imagePath);
            review.addImage(image);
        }
        reviewRepository.save(review);

        request.getBreadRatingList().forEach(breadRatingRequest -> {
            Bread bread = breadRepository.findById(breadRatingRequest.getBreadId()).orElseThrow(BreadNotFoundException::new);
            if(breadRatingRepository.findByBreadAndReview(bread, review).isEmpty()) {
                BreadRating breadRating = BreadRating.builder()
                        .bread(bread).review(review).rating(breadRatingRequest.getRating()).build();
                breadRatingRepository.save(breadRating);
                review.addRating(breadRating);
            }
        });
    }

    @Transactional
    public void removeReview(String username, Long reviewId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Review review = reviewRepository.findByIdAndUserAndIsUseIsTrue(reviewId, user).orElseThrow(ReviewNotFoundException::new);
//        reviewRepository.delete(review);
        review.useChange();
    }

//    @Transactional(readOnly = true)
//    public List<UserReviewDto> getUserReviewList(String username) {
//        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
//        return reviewRepository.findByUserId(user.getId())
//                .stream().filter(Review::isUse).map(UserReviewDto::new)
//                .sorted(Comparator.comparing(UserReviewDto::getId).reversed())
//                .collect(Collectors.toList());
//    }

    @Transactional
    public void reviewLike(String username, Long reviewId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Review review = reviewRepository.findByIdAndIsUseIsTrue(reviewId).orElseThrow(ReviewNotFoundException::new);

        if(reviewLikeRepository.findByUserAndReview(user, review).isPresent()) throw new ReviewLikeAlreadyException();

        ReviewLike reviewLike = ReviewLike.builder().review(review).user(user).build();
        review.plusLike(reviewLike);
    }

    @Transactional
    public void reviewUnlike(String username, Long reviewId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Review review = reviewRepository.findByIdAndIsUseIsTrue(reviewId).orElseThrow(ReviewNotFoundException::new);

        ReviewLike reviewLike = reviewLikeRepository.findByUserAndReview(user, review).orElseThrow(ReviewUnlikeAlreadyException::new);
        review.minusLike(reviewLike);
    }

    @Transactional(readOnly = true)
    public List<ReviewCommentDto> getReviewCommentList(Long reviewId) {
        if(reviewRepository.findByIdAndIsUseIsTrue(reviewId).isEmpty()) throw new ReviewNotFoundException();

        return reviewCommentRepository.findByReviewIdAndParentIsNull(reviewId)
                .stream().map(ReviewCommentDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void addReviewComment(String username, Long reviewId, ReviewCommentRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Review review = reviewRepository.findByIdAndIsUseIsTrue(reviewId).orElseThrow(ReviewNotFoundException::new);

        if(request.getParentCommentId().equals(0L)) { // ??????
            ReviewComment reviewComment = ReviewComment.builder()
                    .review(review).user(user).content(request.getContent()).build();
            reviewCommentRepository.save(reviewComment);
            review.addComment(reviewComment); //TODO
        }
        else { // ?????????
            ReviewComment parentComment = reviewCommentRepository.findById(request.getParentCommentId()).orElseThrow(ReviewCommentNotFoundException::new);
            ReviewComment reviewComment = ReviewComment.builder()
                    .review(review).user(user).content(request.getContent()).parent(parentComment).build();
            reviewCommentRepository.save(reviewComment);
            parentComment.addChildComment(reviewComment);
            review.addComment(reviewComment); //TODO
        }
    }

    @Transactional
    public void removeReviewComment(String username, Long reviewId, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        ReviewComment reviewComment = reviewCommentRepository.findByIdAndUser(commentId, user)
                .orElseThrow(ReviewCommentNotFoundException::new);
        Review review = reviewRepository.findByIdAndIsUseIsTrue(reviewId).orElseThrow(ReviewNotFoundException::new);

        if(reviewComment.getParent() == null) { // ?????? ??????
            if(reviewComment.getChildList().isEmpty()) { // ?????? ????????? ?????????
                reviewCommentRepository.delete(reviewComment);
            }
            else { // ?????? ????????? ?????????
                reviewComment.delete();
            }
        }
        else { // ?????? ??????
            if(!reviewComment.getParent().isDelete()) { // ?????? ????????? ?????????, isDelete = false
                reviewComment.getParent().removeChildComment(reviewComment);
                reviewCommentRepository.delete(reviewComment);
            }
            else { // ?????? ????????? ?????????, isDelete = true
                if(reviewComment.getParent().getChildList().size() == 1) { // ?????? ????????? ?????????????????????
                    log.info("THIS!");
                    ReviewComment parent = reviewComment.getParent();
                    parent.removeChildComment(reviewComment);
                    reviewCommentRepository.delete(reviewComment);
                    reviewCommentRepository.delete(parent); // ????????? ??????!!
                    review.removeComment(parent);
                }
                else { // ?????? ????????? ???????????? ?????????
                    reviewComment.getParent().removeChildComment(reviewComment);
                    reviewCommentRepository.delete(reviewComment);
                }
            }
            review.removeComment(reviewComment);
        }
        /*
        1. ?????? ???????????? ?????? ????????? ????????? ??????
        2. ?????? ???????????? ?????? ????????? ????????? isDelete = true
        3. ?????? ???????????? ?????? ????????? ????????? ?????? ????????? ??????
        4. ?????? ???????????? ?????? ????????? isDelete = true ?????? ?????? ????????? ????????????????????? ?????? ???????????? ??????
        5. ?????? ???????????? ?????? ????????? isDelete = true ?????? ?????? ????????? ???????????? ????????? ?????? ????????? ??????
         */
    }


    @Transactional
    public void reviewCommentLike(String username, Long reviewId, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(reviewRepository.findByIdAndIsUseIsTrue(reviewId).isEmpty()) throw new ReviewNotFoundException();
        ReviewComment reviewComment = reviewCommentRepository.findByIdAndUser(commentId, user).orElseThrow(ReviewCommentNotFoundException::new);

        if(reviewCommentLikeRepository.findByUserAndReviewComment(user, reviewComment).isPresent()) throw new ReviewCommentLikeAlreadyException();
        ReviewCommentLike reviewCommentLike = ReviewCommentLike.builder().reviewComment(reviewComment).user(user).build();
        reviewComment.plusLike(reviewCommentLike);
    }

    @Transactional
    public void reviewCommentUnlike(String username, Long reviewId, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(reviewRepository.findByIdAndIsUseIsTrue(reviewId).isEmpty()) throw new ReviewNotFoundException();
        ReviewComment reviewComment = reviewCommentRepository.findByIdAndUser(commentId, user).orElseThrow(ReviewCommentNotFoundException::new);

        ReviewCommentLike reviewCommentLike = reviewCommentLikeRepository.findByUserAndReviewComment(user, reviewComment).orElseThrow(ReviewCommentUnlikeAlreadyException::new);
        reviewComment.minusLike(reviewCommentLike);
        reviewCommentLikeRepository.delete(reviewCommentLike);
    }

    @Transactional
    public void reviewReport(Long reviewId, ReviewReportRequest request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);

        ReviewReport reviewReport = ReviewReport.builder()
                .review(review).reason(request.getReason()).content(request.getContent()).build();

        reviewReportRepository.save(reviewReport);
    }
}
