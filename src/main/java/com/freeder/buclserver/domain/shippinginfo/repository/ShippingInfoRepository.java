package com.freeder.buclserver.domain.shippinginfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;

public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Integer> {
}
