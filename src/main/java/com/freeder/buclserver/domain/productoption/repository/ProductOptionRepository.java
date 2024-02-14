package com.freeder.buclserver.domain.productoption.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.productoption.entity.ProductOption;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

	Optional<ProductOption> findByProductAndSkuCodeAndIsExposed(Product product, Long skuCode, boolean exposed);

	// Optional<ProductOption> findByProduct(Product product);
}
