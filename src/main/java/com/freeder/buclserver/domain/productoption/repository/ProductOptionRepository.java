package com.freeder.buclserver.domain.productoption.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.productoption.entity.ProductOption;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

	@Query("SELECT po FROM ProductOption po " +
		"WHERE po.product.productCode = :productCode " +
		"AND po.product.deletedAt IS NULL " +
		"AND po.product.isExposed = true " +
		"AND po.product.productStatus = com.freeder.buclserver.domain.product.vo.ProductStatus.ACTIVE")
	List<ProductOption> findByProductProductCodeWithConditions(@Param("productCode") Long productCode);
}
