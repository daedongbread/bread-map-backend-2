package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.review.BreadReview;
import com.depromeet.breadmapbackend.domain.review.repository.BreadReviewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.review.DataNotExistedException;
import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;
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

    public List<SimpleReviewDto> getAllReviewList() {

        return queryFactory.select(Projections.fields(SimpleReviewDto.class,
                        breadReview.id,
                        breadReview.createdAt,
                        breadReview.modifiedAt,
                        breadReview.content,
                        breadReview.imageList,
                        breadReview.rating,
                        breadReview.bakery.id,
                        breadReview.bread.id,
                        breadReview.user.id))
                        .from(breadReview)
                        .fetch();
    }

    public Object addReview(long userId, long bakeryId, long breadId, String content, Integer rating) throws DataNotExistedException {

        if(!userRepository.existsById(userId)) {
            throw new DataNotExistedException("User is not existed");
        } else if(!bakeryRepository.existsById(bakeryId)) {
            throw new DataNotExistedException("Bakery is not existed");
        } else if(!breadRepository.existsById(breadId)){
            throw new DataNotExistedException("Request Body is wrong");
        } else {
            User user = userRepository.getById(userId);
            Bakery bakery = bakeryRepository.getById(bakeryId);
            Bread bread = breadRepository.getById(breadId);

            BreadReview breadReviewQuery = BreadReview.builder()
                    .user(user)
                    .bakery(bakery)
                    .bread(bread)
                    .content(content)
                    .rating(rating)
                    .imageList(null)
                    .build();

            breadReviewRepository.save(breadReviewQuery);

            return "Add review successfully";
        }
    }
}
