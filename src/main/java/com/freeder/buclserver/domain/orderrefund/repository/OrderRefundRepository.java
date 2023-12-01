package com.freeder.buclserver.domain.orderrefund.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;

public interface OrderRefundRepository extends JpaRepository<OrderRefund, Long> {
}
