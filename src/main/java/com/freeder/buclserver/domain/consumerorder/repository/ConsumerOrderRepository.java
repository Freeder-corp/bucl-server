package com.freeder.buclserver.domain.consumerorder.repository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsumerOrderRepository extends JpaRepository<ConsumerOrder,Long> {
    Optional<ConsumerOrder> findByOrderCode(String orderCode);
}
