package com.freeder.buclserver.domain.reward.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.reward.entity.Reward;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
	List<Reward> findByUserId(Long userId);

	List<Reward> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

}
