package com.freeder.buclserver.domain.consumerorder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;

public interface ConsumerOrderRepository extends JpaRepository<ConsumerOrder, Long> {
	Optional<ConsumerOrder> findByOrderCode(String orderCode);

	List<ConsumerOrder> findByProduct_IdAndOrderStatusOrderByCreatedAtDesc(Long productId,
		OrderStatus orderStatus);
}
