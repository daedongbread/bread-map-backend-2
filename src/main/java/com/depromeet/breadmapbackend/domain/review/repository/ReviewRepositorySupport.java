package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.BreadRepository;
import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.BreadReview;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.review.DataNotExistedException;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewDTO;
import com.google.gson.JsonArray;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.depromeet.breadmapbackend.domain.review.QBreadReview.breadReview;
import static com.depromeet.breadmapbackend.domain.review.QBreadRating.breadRating;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewRepositorySupport {
    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;
    private final BakeryRepository bakeryRepository;
    private final BreadRepository breadRepository;
    private final BreadReviewRepository breadReviewRepository;
    private final BreadRatingRepositroy breadRatingRepositroy;

    public List<ReviewDTO> getAllReviewList() {

        Map<BreadReview, List<BreadRating>> transform = queryFactory
                .from(breadReview)
                .leftJoin(breadReview.ratings, breadRating)
                .transform(groupBy(breadReview).as(list(breadRating)));

        return transform.entrySet().stream()
                .map(entry -> new ReviewDTO(entry.getKey()))
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getBakeryReviewList(long bakeryId) {
        Map<BreadReview, List<BreadRating>> transform = queryFactory
                .from(breadReview)
                .leftJoin(breadReview.ratings, breadRating)
                .where(breadReview.bakery.id.eq(bakeryId))
                .transform(groupBy(breadReview).as(list(breadRating)));

        return transform.entrySet().stream()
                .map(entry -> new ReviewDTO(entry.getKey()))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public Object addReview(Map<String, Object> data) throws DataNotExistedException {

        long userId = Long.parseLong(String.valueOf(data.get("userId")));
        long bakeryId = Long.parseLong(String.valueOf(data.get("bakeryId")));
        List<Long> breadId = (List<Long>) data.get("breadId");
        String content = (String) data.get("content");
        List<Long> rating = (ArrayList<Long>) data.get("rating");

        if(!userRepository.existsById(userId)) {
            throw new DataNotExistedException("User is not existed");
        } else if(!bakeryRepository.existsById(bakeryId)) {
            throw new DataNotExistedException("Bakery is not existed");
        } else {
            User user = userRepository.getById(userId);
            Bakery bakery = bakeryRepository.getById(bakeryId);

            BreadReview breadReviewQuery = BreadReview.builder()
                    .user(user)
                    .bakery(bakery)
                    .content(content)
                    .imageList(null)
                    .isUse(true)
                    .build();

            breadReviewRepository.save(breadReviewQuery);

            Long breadReviewPK = breadReviewQuery.getId(); //Insert 한 review의 id를 가져온다.

            for(int i = 0; i < breadId.size(); i++) {
                if(!breadRepository.existsById(Long.parseLong(String.valueOf(breadId.get(i))))) {
                    throw new DataNotExistedException("Bread is not existed");
                } else {
                    Bread bread = breadRepository.getById(Long.parseLong(String.valueOf(breadId.get(i))));
                    BreadReview breadReview = breadReviewRepository.getById(breadReviewPK);

                    BreadRating breadRatingQuery = BreadRating.builder()
                            .user(user)
                            .bakery(bakery)
                            .bread(bread)
                            .breadReview(breadReview)
                            .rating(Long.parseLong(String.valueOf(rating.get(i))))
                            .isUse(true)
                            .build();

                    breadRatingRepositroy.save(breadRatingQuery);
                }
            }

            return "Add review successfully";
        }
    }
}
