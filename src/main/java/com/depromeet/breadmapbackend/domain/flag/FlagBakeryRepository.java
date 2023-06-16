package com.depromeet.breadmapbackend.domain.flag;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.user.User;

public interface FlagBakeryRepository extends JpaRepository<FlagBakery, Long> {
	@Query("SELECT count(*) FROM FlagBakery fb INNER JOIN Flag f ON f = fb.flag WHERE fb.bakery = :bakery AND f.name = '가봤어요'")
	int countFlagNum(@Param("bakery") Bakery bakery);

	@Query("SELECT fb FROM FlagBakery fb JOIN FETCH fb.bakery b JOIN FETCH fb.flag f WHERE f = :flag AND b.status = 'POSTING' ORDER BY fb.createdAt DESC")
	List<FlagBakery> findByFlagAndStatusIsPostingOrderByCreatedAtDesc(@Param("flag") Flag flag);

	Optional<FlagBakery> findByBakeryAndFlagAndUser(Bakery bakery, Flag flag, User user);

	Optional<FlagBakery> findByBakeryAndUser(Bakery bakery, User user);

	@Query("SELECT fb.flag FROM FlagBakery fb where fb.bakery = ?1 and fb.user = ?2")
	Optional<Flag> findFlagByBakeryAndUser(Bakery bakery, User user);

	@Transactional
	void delete(FlagBakery flagBakery);

	Optional<FlagBakery> findByBakeryIdAndFlagIdAndUserId(Long bakeryId, Long flagId, Long userId);

	@Query(
		"select fb "
			+ "from FlagBakery fb "
			+ "join fetch fb.bakery b "
			+ "where fb.flag = :flag "
			+ "and fb.user.id = :userId "
			+ "AND b.status = 'POSTING' "
			+ "ORDER BY fb.createdAt DESC "
	)
	List<FlagBakery> findByFlagAndUserIdOrderByCreatedAtDesc(
		@Param("flag") Flag flag,
		@Param("userId") Long userId
	);

	@Query("SELECT fb.bakery.id as bakeryId, "
		+ " count(fb.bakery) "
		+ "FROM FlagBakery fb "
		+ "INNER JOIN Flag f ON f = fb.flag "
		+ "WHERE fb.bakery in (:bakeryList)  "
		+ "AND f.name = '가봤어요' "
		+ "group by fb.bakery.id")
	List<BakeryCountInFlag> countFlagNum(@Param("bakeryList") List<Bakery> bakeryList);

	interface BakeryCountInFlag {
		Long getBakeryId();

		Long getCount();

	}
}
