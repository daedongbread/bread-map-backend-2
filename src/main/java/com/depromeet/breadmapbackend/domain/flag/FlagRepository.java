package com.depromeet.breadmapbackend.domain.flag;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.depromeet.breadmapbackend.domain.user.User;

public interface FlagRepository extends JpaRepository<Flag, Long> {
	Optional<Flag> findByUserAndName(User user, String name);

	Optional<Flag> findByUserIdAndId(Long userId, Long flagId);

	List<Flag> findByUserId(Long userId);

	void deleteByUser(User user);

	Optional<Flag> findByIdAndUserId(Long flagId, Long userId);

}
