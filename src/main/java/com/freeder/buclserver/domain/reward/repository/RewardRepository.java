package com.freeder.buclserver.domain.reward.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.vo.RewardType;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
	List<Reward> findByUserId(Long userId);

	List<Reward> findByUserIdAndType(Long userId, RewardType type);
}
