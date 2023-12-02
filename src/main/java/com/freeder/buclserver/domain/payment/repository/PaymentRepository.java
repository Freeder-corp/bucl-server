package com.freeder.buclserver.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Payment findByConsumerOrder(ConsumerOrder consumerOrder);
}
