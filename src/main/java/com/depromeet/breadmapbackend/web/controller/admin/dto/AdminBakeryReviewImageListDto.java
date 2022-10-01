package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class AdminBakeryReviewImageListDto {
    private Integer numberOfElements;
    private Boolean hasNext;
    private List<AdminBakeryReviewImageDto> imageList;

    @Builder
    public AdminBakeryReviewImageListDto(Slice<ReviewImage> images) {
        this.numberOfElements = images.getNumberOfElements();
        this.hasNext = images.hasNext();
        this.imageList = images.stream().map(AdminBakeryReviewImageDto::new).collect(Collectors.toList());
    }
}
