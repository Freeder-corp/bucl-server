package com.freeder.buclserver.domain.consumerorder.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.user.entity.User;

@Repository
public interface ConsumerOrderRepository extends JpaRepository<ConsumerOrder, Long> {
	Page<ConsumerOrder> findAllByConsumerOrderByCreatedAtDesc(User consumer, Pageable pageable);

	Optional<ConsumerOrder> findByOrderCode(String orderCode);
}
