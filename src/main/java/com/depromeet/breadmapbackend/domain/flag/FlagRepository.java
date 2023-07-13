package com.depromeet.breadmapbackend.domain.flag;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.user.User;

public interface FlagRepository extends JpaRepository<Flag, Long> {
	Optional<Flag> findByUserAndName(User user, String name);

	Optional<Flag> findByUserIdAndId(Long userId, Long flagId);

	List<Flag> findByUserId(Long userId);

	void deleteByUser(User user);

	Optional<Flag> findByIdAndUserId(Long flagId, Long userId);

	@Query("SELECT fb.bakery.id as bakeryId, "
		+ " count(fb.bakery) "
		+ "FROM Flag f "
		+ "INNER JOIN FlagBakery fb ON f = fb.flag "
		+ "WHERE fb.bakery in (:bakeryList)  "
		+ "AND f.name = '가봤어요' "
		+ "group by fb.bakery.id")
	List<BakeryCountInFlag> countFlagNum(@Param("bakeryList") List<Bakery> bakeryList);

	interface BakeryCountInFlag {
		Long getBakeryId();

		Long getCount();

	}
}
