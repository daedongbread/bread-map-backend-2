package com.depromeet.breadmapbackend.domain.challenge.breaddiary.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public record AddBreadDiaryRequest(
        @NotNull MultipartFile image,
        long bakeryId,
        @NotNull String productName,
        int productPrice,
        @Min(1) @Max(5) int rating,
        @NotEmpty Set<Long> bakeryTags,
        @NotEmpty Set<Long> breadTags) {
}
