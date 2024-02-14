package com.freeder.buclserver.domain.grouporder.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import com.freeder.buclserver.domain.product.entity.Product;

public interface GroupOrderRepository extends JpaRepository<GroupOrder, Long> {

	List<GroupOrder> findByIsActive(boolean isActive);

	Optional<GroupOrder> findByProductAndIsActiveIsTrue(Product product);

	@Query("SELECT go FROM GroupOrder go " + "WHERE go.product.productCode = :productCode "
		+ "AND go.isActive = :isActive "
		+ "AND :createdAt between go.startedAt AND go.endedAt ")
	Optional<GroupOrder> findByProductAndIsActiveAndCreatedBetween(@Param("productCode") Long productCode,
		@Param("isActive") boolean isActive, @Param("createdAt") LocalDateTime createdAt);
}
