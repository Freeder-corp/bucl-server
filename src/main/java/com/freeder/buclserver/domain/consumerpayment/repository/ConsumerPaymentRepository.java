package com.freeder.buclserver.domain.consumerpayment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.consumerpayment.entity.ConsumerPayment;

public interface ConsumerPaymentRepository extends JpaRepository<ConsumerPayment, Long> {
}
