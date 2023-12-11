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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		try {
			Pageable pageable = PageRequest.of(page, pageSize);
			List<Reward> rewardHistory = rewardRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

			log.info("적립금 내역 조회 성공 - userId: {}, page: {}, pageSize: {}", userId, page, pageSize);

			return rewardHistory.stream()
				.map(this::mapRewardToDto)
				.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("적립금 내역 조회 실패 - userId: {}, page: {}, pageSize: {}", userId, page, pageSize, e);
			throw e;
		}
	}

	private RewardDto mapRewardToDto(Reward reward) {
		return new RewardDto(
			reward.getProductBrandName(),
			reward.getProductName(),
			calculateNetReward(reward),
			reward.getRewardType(),
			reward.getCreatedAt()
		);
	}

	private int calculateNetReward(Reward reward) {
		try {
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
		} catch (Exception e) {
			log.error("적립금 계산 실패 - userId: {}, rewardId: {}", reward.getUser().getId(), reward.getId(), e);
			throw e;
		}
	}

}
