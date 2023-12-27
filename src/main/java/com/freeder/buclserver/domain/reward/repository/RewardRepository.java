package com.freeder.buclserver.domain.reward.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.freeder.buclserver.domain.reward.entity.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long> {

	@Query("SELECT COALESCE(r.rewardSum, 0) FROM Reward r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
	List<Integer> findUserRewardSum(Long userId, Pageable pageable);
}
