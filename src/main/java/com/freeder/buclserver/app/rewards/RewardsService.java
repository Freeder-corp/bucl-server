package com.freeder.buclserver.app.rewards;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.reward.dto.RewardDto;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.reward.vo.RewardType;

@Service
public class RewardsService {

	@Autowired
	private RewardRepository rewardRepository;

	@Transactional(readOnly = true)
	public Integer getUserRewardCrntAmount(Long userId) {
		return rewardRepository.findFirstByUserId(userId).orElse(0).intValue();
	}

	@Transactional(readOnly = true)
	public List<RewardDto> getRewardHistoryPageable(Long userId, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		Pageable pageable = PageRequest.of(offset, pageSize);
		List<Reward> rewardHistory = rewardRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

		return rewardHistory.stream()
			.map(this::mapRewardToDto)
			.collect(Collectors.toList());
	}

	private RewardDto mapRewardToDto(Reward reward) {
		return new RewardDto(
			reward.getProductBrandName(),
			reward.getProductName(),
			calculateNetReward(reward),
			reward.getCreatedAt()
		);
	}

	private int calculateNetReward(Reward reward) {
		if (RewardType.BUSINESS.equals(reward.getRewardType()) ||
			RewardType.CONSUMER.equals(reward.getRewardType()) ||
			RewardType.REFUND.equals(reward.getRewardType())) {
			return reward.getReceivedRewardAmount();
		} else if (RewardType.SPEND.equals(reward.getRewardType()) ||
			RewardType.WITHDRAWAL.equals(reward.getRewardType())) {
			return -reward.getSpentRewardAmount();
		} else {
			return 0;
		}
	}

}
