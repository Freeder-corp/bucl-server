package com.freeder.buclserver.domain.shippinginfo.repository;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;

public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Integer> {
	Optional<ShippingInfo> findById(@NotNull Integer integer);
}
