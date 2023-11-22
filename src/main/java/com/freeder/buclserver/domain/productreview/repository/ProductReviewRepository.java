package com.freeder.buclserver.domain.productreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.productreview.entity.ProductReview;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

	@Query("SELECT COUNT(r) FROM ProductReview r WHERE r.product.id = :productId")
	long countByProductIdFk(@Param("productId") Long productId);
}