package com.freeder.buclserver.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findByProductCode(Long productCode);

	List<Product> findByIsExposed(boolean isExposed);
}
