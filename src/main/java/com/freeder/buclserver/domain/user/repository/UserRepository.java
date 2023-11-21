package com.freeder.buclserver.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findBySocialId(String socialId);

	Optional<User> findByRefreshToken(String refreshToken);
}