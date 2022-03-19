package com.depromeet.breadmapbackend.domain.flag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FlagRepository extends JpaRepository<Flag, Long>, FlagRepositoryCustom {

}
