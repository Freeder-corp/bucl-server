package com.freeder.buclserver.domain.productreview.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.productreview.entity.ProductReview;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

	@Query("SELECT COUNT(pr) FROM ProductReview pr " +
		"WHERE pr.product.productCode = :productCode " +
		"AND pr.product.deletedAt IS NULL " +
		"AND pr.product.isExposed = true " +
		"AND pr.product.productStatus = com.freeder.buclserver.domain.product.vo.ProductStatus.ACTIVE")
	long countByProductCodeFkWithConditions(@Param("productCode") Long productCode);

	@Query("SELECT pr FROM ProductReview pr " +
		"WHERE pr.product.productCode = :productCode " +
		"AND pr.product.deletedAt IS NULL " +
		"AND pr.product.isExposed = true " +
		"AND pr.product.productStatus = com.freeder.buclserver.domain.product.vo.ProductStatus.ACTIVE " +
		"ORDER BY pr.starRate DESC, pr.createdAt DESC")
	Page<ProductReview> findByProductProductCodeWithConditions(
		@Param("productCode") Long productCode,
		Pageable pageable
	);
}


