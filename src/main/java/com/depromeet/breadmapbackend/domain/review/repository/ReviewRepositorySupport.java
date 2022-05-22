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
import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.depromeet.breadmapbackend.domain.review.QBreadReview.breadReview;

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

    public List<SimpleReviewDto> getAllReviewList() {
        return queryFactory.select(Projections.fields(SimpleReviewDto.class,
                        breadReview.id,
                        breadReview.createdAt,
                        breadReview.modifiedAt,
                        breadReview.content,
                        breadReview.imageList,
                        ExpressionUtils.as(breadReview.bakery.id, "bakery_id"),
                        ExpressionUtils.as(breadReview.user.id, "user_id"),
                        breadReview.isUse))
                .from(breadReview)
                .where(breadReview.isUse.eq(true))
                .fetch();
    }

    public Object addReview(long userId, long bakeryId, String breadId, String content, String rating) throws DataNotExistedException {

        String[] breadIdToArray = breadId.replaceAll("\\[", "").replaceAll("]", "").split(",");
        Long[] breadArray = new Long[breadIdToArray.length];

        String[] ratingToArray = rating.replaceAll("\\[", "").replaceAll("]", "").split(",");
        Integer[] ratingArray = new Integer[ratingToArray.length];

        for(int i = 0; i < breadArray.length; i++) {
            try {
                breadArray[i] = Long.parseLong(breadIdToArray[i]);
                ratingArray[i] = Integer.parseInt(ratingToArray[i]);
            } catch (Exception e) {
                throw new DataNotExistedException("Unable to parse data");
            }
        }

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

            List<Long> breadReviewPK = queryFactory.select(breadReview.id.max())
                    .from(breadReview)
                    .where(breadReview.isUse.eq(true))
                    .fetch();

            for(int i = 0; i < breadArray.length; i++) {
                if(!breadRepository.existsById(breadArray[i])) {
                    throw new DataNotExistedException("Bread is not existed");
                } else {
                    Bread bread = breadRepository.getById(breadArray[i]);
                    BreadReview breadReview = breadReviewRepository.getById(breadReviewPK.get(0));

                    BreadRating breadRatingQuery = BreadRating.builder()
                            .user(user)
                            .bakery(bakery)
                            .bread(bread)
                            .breadReview(breadReview)
                            .rating(ratingArray[i])
                            .isUse(true)
                            .build();

                    breadRatingRepositroy.save(breadRatingQuery);
                }
            }

            return "Add review successfully";
        }
    }
}
