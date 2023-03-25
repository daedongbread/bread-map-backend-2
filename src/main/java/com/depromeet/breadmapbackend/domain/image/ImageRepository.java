package com.depromeet.breadmapbackend.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByHashValue(String hashValue);
}
