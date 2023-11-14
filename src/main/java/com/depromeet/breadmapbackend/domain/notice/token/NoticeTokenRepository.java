package com.depromeet.breadmapbackend.domain.notice.token;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.depromeet.breadmapbackend.domain.user.User;

public interface NoticeTokenRepository extends JpaRepository<NoticeToken, Long> {
	void deleteByUser(User user);

	void deleteByDeviceToken(String deviceToken);

	Optional<NoticeToken> findByDeviceToken(String deviceToken);

	List<NoticeToken> findAllByDeviceToken(String deviceToken);

	@Query("select nt from NoticeToken nt where nt.user.id = :userId")
	List<NoticeToken> findByUser(@Param("userId") Long user);

	Optional<NoticeToken> findByUserAndDeviceToken(User user, String deviceToken);

	@Query("select nt "
		+ "from NoticeToken nt "
		+ "join fetch nt.user u "
		+ "where u in :noticeSendUsers "
		+ "and nt.deviceToken is not null")
	List<NoticeToken> findByUserIn(@Param("noticeSendUsers") List<User> noticeSendUsers);

}
