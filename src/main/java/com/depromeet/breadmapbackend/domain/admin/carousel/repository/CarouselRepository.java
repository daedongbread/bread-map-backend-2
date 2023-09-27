package com.depromeet.breadmapbackend.domain.admin.carousel.repository;

import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselManager;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CarouselRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/09/20
 */
public interface CarouselRepository extends JpaRepository<CarouselManager, Long> {

    List<CarouselManager> findByCarouseledIsOrderByCarouselOrderAsc(final boolean carouseled);

    Optional<CarouselManager> findByTargetIdAndCarouselType(final Long targetId, final CarouselType carouselType);

    List<CarouselManager> findByTargetIdIn(List<Long> targetIds);
}
