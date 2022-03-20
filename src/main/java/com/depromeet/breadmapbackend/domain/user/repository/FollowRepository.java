package com.depromeet.breadmapbackend.domain.user.repository;

import com.depromeet.breadmapbackend.domain.user.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

}
