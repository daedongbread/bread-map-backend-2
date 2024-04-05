package com.depromeet.breadmapbackend.domain.challenge.breaddiary.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record AddBreadDiaryDto(
        String oAuthId,
        MultipartFile image,
        long bakeryId,
        String productName,
        int productPrice,
        int rating,
        Set<Long> bakeryTags,
        Set<Long> productTags) {
}
