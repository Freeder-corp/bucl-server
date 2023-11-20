package com.freeder.buclserver.domain.shipping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.shipping.entity.Shipping;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {
}
