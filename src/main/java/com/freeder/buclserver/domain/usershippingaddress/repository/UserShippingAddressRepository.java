package com.freeder.buclserver.domain.usershippingaddress.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.usershippingaddress.entity.UserShippingAddress;

public interface UserShippingAddressRepository extends JpaRepository<UserShippingAddress, Long> {

	Optional<UserShippingAddress> findByUserAndIsDefaultAddressIsTrue(User user);

	List<UserShippingAddress> findAllByUser(User user);

	boolean existsByUser(User user);

	Optional<UserShippingAddress> findFirstByUser_IdOrderByIdDesc(Long userId);
}
