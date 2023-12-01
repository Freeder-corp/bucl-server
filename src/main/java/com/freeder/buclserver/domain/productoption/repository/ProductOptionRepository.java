package com.freeder.buclserver.domain.productoption.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.productoption.entity.ProductOption;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

	List<ProductOption> findByProduct_productCode(Long productCode);
}