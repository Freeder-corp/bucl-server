package com.freeder.buclserver.domain.consumerorder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.user.entity.User;

@Repository
public interface ConsumerOrderRepository extends JpaRepository<ConsumerOrder, Long> {

	int countByGroupOrderId(Long groupOrderId);

	@EntityGraph(attributePaths = {"product"})
	List<ConsumerOrder> findAllByConsumerOrderByCreatedAtDesc(User user, Pageable pageable);

	@EntityGraph(attributePaths = {"product"})
	Optional<ConsumerOrder> findById(Long consumerOrderId);
}
