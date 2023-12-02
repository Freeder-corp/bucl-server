package com.freeder.buclserver.domain.orderexchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.orderexchange.entity.OrderExchange;

public interface OrderExchangeRepository extends JpaRepository<OrderExchange, Long> {
	
}
