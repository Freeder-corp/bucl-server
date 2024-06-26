package com.freeder.buclserver.domain.shipping.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.shipping.entity.Shipping;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {

	@EntityGraph(attributePaths = {"shippingInfo"})
	Optional<Shipping> findByConsumerOrderAndIsActiveIsTrue(ConsumerOrder consumerOrder);
}
