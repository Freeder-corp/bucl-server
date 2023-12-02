package com.freeder.buclserver.domain.orderreturn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.orderreturn.entity.OrderReturn;

public interface OrderReturnRepository extends JpaRepository<OrderReturn, Long> {
}
