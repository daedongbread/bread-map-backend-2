package com.depromeet.breadmapbackend.domain.bakery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BakeryRepository extends JpaRepository<Bakery, Long>{
    Optional<Bakery> findByIdAndStatus(Long id, BakeryStatus status);

    List<Bakery> findTop20ByLatitudeBetweenAndLongitudeBetweenAndStatus(Double leftLatitude, Double rightLatitude, Double downLongitude, Double upLongitude, BakeryStatus status);

//    @Query("select b from Bakery b" +
//            " join fetch b.reviewList" +
//            " where b.latitude between :leftLatitude and :rightLatitude" +
//            " and b.longitude between :downLongitude and :upLongitude" +
//            " and b.status = :status")
//    List<Bakery> findTop20ByLatitudeBetweenAndLongitudeBetweenAndStatus2(@Param("leftLatitude")Double leftLatitude, @Param("rightLatitude")Double rightLatitude, @Param("downLongitude")Double downLongitude, @Param("upLongitude")Double upLongitude, @Param("status")BakeryStatus status);

    @Query(value = "select * from bakery b ORDER BY b.modified_At DESC", countQuery = "select count(*) from bakery", nativeQuery = true)
    Page<Bakery> findPageAll(Pageable pageable);

    @Query(value = "SELECT * FROM bakery b WHERE name LIKE %:name% AND status = 'POSTING' ORDER BY (acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude))) * 6371000) LIMIT :limit", nativeQuery = true)
    List<Bakery> find10ByNameContainsIgnoreCaseAndStatusOrderByDistance(@Param("name") String name, @Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("limit") int limit);

    @Query(value = "select * from bakery b where b.name like %:name% ORDER BY b.modified_At DESC", countQuery = "select count(*) from bakery b where b.name like %:name%", nativeQuery = true)
    Page<Bakery> findByNameContainsOrderByUpdatedAt(@Param("name") String name, Pageable pageable);

    Long countByStatus(BakeryStatus status);
}
