package com.freeder.buclserver.domain.consumerorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;

@Repository
public interface ConsumerOrderRepository extends JpaRepository<ConsumerOrder, Long> {

	int countByGroupOrderId(Long groupOrderId);
}
