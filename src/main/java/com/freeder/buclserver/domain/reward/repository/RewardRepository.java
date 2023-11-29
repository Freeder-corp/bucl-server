package com.freeder.buclserver.domain.reward.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.reward.entity.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long> {

	@EntityGraph(attributePaths = {"user"})
	Optional<Reward> findFirstByUser_IdOrderByCreatedAtDesc(Long userId);
}
