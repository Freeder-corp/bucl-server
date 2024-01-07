package com.freeder.buclserver.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.vo.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByIdAndRole(Long userId, Role role);
}
