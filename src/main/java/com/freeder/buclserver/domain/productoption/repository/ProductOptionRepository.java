package com.freeder.buclserver.domain.productoption.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.productoption.entity.ProductOption;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
	Optional<ProductOption> findBySkuCode(String skuCode);

	// Optional<ProductOption> findByProduct(Product product);
}
