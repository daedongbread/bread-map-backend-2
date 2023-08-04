package com.depromeet.breadmapbackend.domain.review;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.depromeet.breadmapbackend.domain.bakery.product.Product;

public interface ReviewProductRatingRepository extends JpaRepository<ReviewProductRating, Long> {
	//    @Query("SELECT AVG(rpr.rating) FROM ReviewProductRating rpr WHERE rpr.product.id = ?1")
	//    Optional<Double> findProductAvgRating(Long productId);
	Optional<ReviewProductRating> findByProductAndReview(Product product, Review review);

	void deleteByProductId(Long productId);

	void deleteByBakeryId(Long bakeryId);

	@Query(
		"select avg(r.rating)  "
			+ "from ReviewProductRating r "
			+ "where r.bakery.id = :bakeryId "
	)
	Double findAvgRatingByBakeryId(@Param("bakeryId") Long bakeryId);
}
