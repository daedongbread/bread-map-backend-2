package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByIdAndIsBlockIsFalseAndIsDeleteIsFalse(Long id);
    List<Review> findByBakery(Bakery bakery);
    Page<Review> findPageByBakeryAndIsHideIsFalseAndIsDeleteIsFalse(Bakery bakery, Pageable pageable);
//    @Query(value = "SELECT r FROM Review r JOIN r.ratings pr WHERE pr.product = :product AND r.bakery = :bakery AND r.status = 'UNBLOCK'")
//    Slice<Review> findSliceByBakeryAndProductOrder(@Param("bakery")Bakery bakery, @Param("product")Product product, Pageable pageable);
//    @Query(value = "SELECT r FROM Review r JOIN r.ratings pr WHERE pr.product = :product AND r.bakery = :bakery AND r.status = 'UNBLOCK' GROUP BY r.id ORDER BY AVG(pr.rating) DESC, r.createdAt DESC")
//    Slice<Review> findSliceByBakeryAndProductOrderByRatingDesc(@Param("bakery")Bakery bakery, @Param("product")Product product, Pageable pageable);
//    @Query(value = "SELECT r FROM Review r JOIN r.ratings pr WHERE pr.product = :product AND r.bakery = :bakery AND r.status = 'UNBLOCK' GROUP BY r.id ORDER BY AVG(pr.rating) ASC, r.createdAt DESC")
//    Slice<Review> findSliceByBakeryAndProductOrderByRatingAsc(@Param("bakery")Bakery bakery, @Param("product")Product product, Pageable pageable);
    List<Review> findByUser(User user);
    void deleteByUser(User user);
    Optional<Review> findByIdAndBakery(Long id, Bakery bakery);
    Optional<Review> findByIdAndUser(Long id, User user);
    Integer countByUser(User user);
    Integer countByBakeryAndIsNewIsTrue(Bakery bakery);
    Integer countByIsNewIsTrue();
}
