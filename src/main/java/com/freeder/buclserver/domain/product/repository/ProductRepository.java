package com.freeder.buclserver.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;

public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query("SELECT p FROM Product p " +
		"WHERE p.productCategory.id = :categoryId " +
		"AND p.deletedAt IS NULL " +
		"AND p.isExposed = true " +
		"AND p.productStatus = com.freeder.buclserver.domain.product.vo.ProductStatus.ACTIVE " +
		"ORDER BY p.productPriority DESC")
	Optional<Page<Product>> findProductsByConditions(
		@Param("categoryId") Long categoryId,
		Pageable pageable
	);

	@Query("SELECT p FROM Product p " +
		"WHERE p.productCode = :productCode " +
		"AND p.deletedAt IS NULL " +
		"AND p.isExposed = true " +
		"AND p.productStatus = com.freeder.buclserver.domain.product.vo.ProductStatus.ACTIVE")
	Optional<Product> findAvailableProductByCode(@Param("productCode") Long productCode);

	@Query("SELECT pr FROM ProductReview pr " +
		"WHERE pr.product.productCode = :productCode")
	List<ProductReview> findReviewsByProductCode(@Param("productCode") Long productCode);

	@Query("SELECT p FROM Product p " +
		"WHERE p.productCategory.id = :categoryId " +
		"ORDER BY p.consumerPrice * p.consumerRewardRate DESC, p.createdAt DESC")
	Page<Product> findProductsOrderByReward(
		@Param("categoryId") Long categoryId,
		Pageable pageable
	);

	Optional<Product> findByProductCode(Long productCode);
}


