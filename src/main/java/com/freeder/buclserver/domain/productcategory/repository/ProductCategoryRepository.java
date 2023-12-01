package com.freeder.buclserver.domain.productcategory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freeder.buclserver.domain.product.entity.Product;

public interface ProductCategoryRepository extends JpaRepository<Product, Long> {
	@Query("SELECT p FROM Product p " +
		"WHERE p.productCategory.id = :categoryId " +
		"ORDER BY p.consumerPrice * p.consumerRewardRate DESC, p.createdAt DESC")
	Page<Product> findProductsByCategory(
		@Param("categoryId") Long categoryId,
		Pageable pageable
	);
}


