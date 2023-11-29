package com.freeder.buclserver.domain.reward.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.user.entity.User;

public interface RewardRepository extends JpaRepository<Reward, Long> {
	Optional<Reward> findFirstByUserOrderByCreatedAtDesc(User user);
}
