package com.freeder.buclserver.domain.ordercancel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.ordercancel.entity.OrderCancel;

public interface OrderCancelRepository extends JpaRepository<OrderCancel, String> {
	Optional<OrderCancel> findByConsumerOrder(ConsumerOrder consumerOrder);
}
