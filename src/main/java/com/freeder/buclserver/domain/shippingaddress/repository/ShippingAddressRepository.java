package com.freeder.buclserver.domain.shippingaddress.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
}
