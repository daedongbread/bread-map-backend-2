package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BakeryRepository extends JpaRepository<Bakery, Long>{
    List<Bakery> findTop20ByLatitudeBetweenAndLongitudeBetween(Double leftLatitude, Double rightLatitude, Double downLongitude, Double upLongitude);
    @Query(value = "select b from Bakery b", countQuery = "select count(*) from Bakery")
    Page<Bakery> findAll(Pageable pageable);
//    Integer countAllByNameEqualsAndStreetAddressEquals(String name, String streetAddress);
    List<Bakery> findByNameStartsWith(String name);
    @Query(value = "select b from Bakery b where b.name like %?1%", countQuery = "select count(b) from Bakery b where b.name like %?1%")
    Page<Bakery> findByNameContains(String name, Pageable pageable);
}
