package com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response;

import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselManager;
import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.post.image.PostImage;
import java.util.List;

/**
 * CreateEventRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public record EventResponse(
    Long managerId,
    boolean isPosted,
    boolean isFixed,
    boolean isCarousel,
    String title,
    String content,
    String bannerImage,
    List<String> images
) {

    public static EventResponse of(final PostManagerMapper managerMapper, CarouselManager carouselManager) {
        return new EventResponse(
            managerMapper.getId(),
            managerMapper.isPosted(),
            managerMapper.isFixed(),
            carouselManager.isCarouseled(),
            managerMapper.getPost().getTitle(),
            managerMapper.getPost().getContent(),
            carouselManager.getBannerImage(),
            managerMapper.getPost().getImages()
                .stream().map(PostImage::getImage).toList()
        );
    }
}
