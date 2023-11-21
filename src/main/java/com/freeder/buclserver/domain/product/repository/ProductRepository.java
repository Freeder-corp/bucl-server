package com.freeder.buclserver.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freeder.buclserver.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT p FROM Product p " +
		"WHERE p.productCategory.id = 1 " +
		"ORDER BY p.consumerPrice * p.consumerRewardRate DESC, p.createdAt DESC")
	List<Product> findHotDealProductsOrderByReward(
		@Param("page") int page,
		@Param("pageSize") int pageSize
	);

	@Query("SELECT p FROM Product p " +
		"WHERE p.productCategory.id = 2 " +
		"ORDER BY p.consumerPrice * p.consumerRewardRate DESC, p.createdAt DESC")
	List<Product> findRewardProductsOrderByReward(
		@Param("page") int page,
		@Param("pageSize") int pageSize
	);
}
