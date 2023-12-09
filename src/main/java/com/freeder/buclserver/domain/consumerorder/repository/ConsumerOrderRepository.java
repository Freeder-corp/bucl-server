package com.freeder.buclserver.domain.consumerorder.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.user.entity.User;

public interface ConsumerOrderRepository extends JpaRepository<ConsumerOrder, Long> {

	@EntityGraph(attributePaths = {"product"})
	Page<ConsumerOrder> findAllByConsumerOrderByCreatedAtDesc(User user, Pageable pageable);

	@EntityGraph(attributePaths = {"product"})
	Optional<ConsumerOrder> findById(Long consumerOrderId);
}
