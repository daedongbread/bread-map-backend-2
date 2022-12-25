package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BakeryRepository extends JpaRepository<Bakery, Long>{
    List<Bakery> findTop20ByLatitudeBetweenAndLongitudeBetween(Double leftLatitude, Double rightLatitude, Double downLongitude, Double upLongitude);
    @Query(value = "select * from bakery", countQuery = "select count(*) from bakery", nativeQuery = true)
    Page<Bakery> findPageAll(Pageable pageable);
//    Integer countAllByNameEqualsAndStreetAddressEquals(String name, String streetAddress);
    List<Bakery> findByNameStartsWith(String name);
    @Query(value = "select * from bakery b where b.name like %:name%", countQuery = "select count(*) from bakery b where b.name like '%' || :name || '%'", nativeQuery = true)
    Page<Bakery> findByNameContains(@Param("name") String name, Pageable pageable);
    Long countByStatus(BakeryStatus status);
}
