package com.freeder.buclserver.domain.shippingaddress.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

	Optional<ShippingAddress> findByShipping(Shipping shipping);
}
