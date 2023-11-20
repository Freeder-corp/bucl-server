package com.freeder.buclserver.domain.consumerpurchaseorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.consumerpurchaseorder.entity.ConsumerPurchaseOrder;

public interface ConsumerPurchaseOrderRepository extends JpaRepository<ConsumerPurchaseOrder, Long> {
}
