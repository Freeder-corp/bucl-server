package com.freeder.buclserver.domain.grouporder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import com.freeder.buclserver.domain.product.entity.Product;

public interface GroupOrderRepository extends JpaRepository<GroupOrder, Long> {
	Optional<GroupOrder> findByProductAndIsEnded(Product product, boolean isEnded);
}
