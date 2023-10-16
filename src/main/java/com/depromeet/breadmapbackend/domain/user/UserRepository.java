package com.depromeet.breadmapbackend.domain.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u FROM User u WHERE u.oAuthInfo.oAuthId = :oAuthId")
	Optional<User> findByOAuthId(@Param("oAuthId") String oAuthId);

	@Query(value = "select u from User u", countQuery = "select count(u) from User u")
	Page<User> findPageAll(Pageable pageable);

	//    Optional<User> findByNickName(String nickName);
	@Query("SELECT u FROM User u WHERE u.userInfo.nickName = :nickName")
	Optional<User> findByNickName(@Param("nickName") String nickName);

	@Query("select u "
		+ "from User u "
		+ "left join fetch u.noticeTokens nt "
		+ "where u.id = :userId ")
	Optional<User> findUserWithNoticeTokensByUserId(@Param("userId") Long userId);

	@Query("select u "
		+ "from User u "
		+ "join fetch u.noticeTokens nt "
		+ "where u.isAlarmOn = true "
		+ "and nt.deviceToken is not null")
	List<User> findUserWithNoticeTokensByIsAlarmOn();

	@Query("select u "
		+ "from User u "
		+ "left join fetch u.noticeTokens nt ")
	List<User> findUserWithNoticeTokens();

	List<User> findByIdNotIn(List<Long> userIds);
}
