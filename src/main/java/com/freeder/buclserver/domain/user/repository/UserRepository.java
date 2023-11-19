package com.freeder.buclserver.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
