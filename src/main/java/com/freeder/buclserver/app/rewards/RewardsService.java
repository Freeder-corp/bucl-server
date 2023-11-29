package com.freeder.buclserver.app.rewards;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.reward.vo.RewardType;

@Service
public class RewardsService {

	@Autowired
	private RewardRepository rewardRepository;

	public List<Reward> getUserRewards(Long userId) {
		return rewardRepository.findByUserId(userId);
	}

	public List<Reward> getUserRewardsByType(Long userId, RewardType type) {
		return rewardRepository.findByUserIdAndType(userId, type);
	}

}
