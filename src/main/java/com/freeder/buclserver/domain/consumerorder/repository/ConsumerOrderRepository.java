package com.freeder.buclserver.domain.consumerorder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;

public interface ConsumerOrderRepository extends JpaRepository<ConsumerOrder, Long> {

	@EntityGraph(attributePaths = {"product"})
	List<ConsumerOrder> findAllByConsumer_IdOrderByCreatedAtDesc(Long userId);

	@EntityGraph(attributePaths = {"product"})
	Optional<ConsumerOrder> findById(Long consumerOrderId);
}
