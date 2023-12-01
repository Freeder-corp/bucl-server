package com.freeder.buclserver.domain.reward.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.user.entity.User;

public interface RewardRepository extends JpaRepository<Reward, Long> {
	Optional<Reward> findFirstByUserOrderByCreatedAtDesc(User user);

	@Query(
		value = "SELECT r.reward_sum FROM Reward r WHERE r.user_id = :userId ORDER BY r.created_at DESC LIMIT 1", nativeQuery = true)
	Optional<Integer> findFirstByUserId(@Param("userId") Long userId);
}
