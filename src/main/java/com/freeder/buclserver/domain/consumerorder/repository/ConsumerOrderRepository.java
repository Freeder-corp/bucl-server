package com.freeder.buclserver.domain.consumerorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;

public interface ConsumerOrderRepository extends JpaRepository<ConsumerOrder, Long> {

}
