package com.freeder.buclserver.app.rewards;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.reward.vo.RewardType;
import com.freeder.buclserver.domain.rewardwithdrawal.dto.WithdrawalHistoryDto;
import com.freeder.buclserver.domain.rewardwithdrawal.entity.RewardWithdrawal;
import com.freeder.buclserver.domain.rewardwithdrawal.repository.RewardWithdrawalRepository;

@Service
public class RewardsWithdrawalService {

	private final RewardWithdrawalRepository rewardWithdrawalRepository;
	private final RewardRepository rewardRepository;

	@Autowired
	public RewardsWithdrawalService(RewardWithdrawalRepository rewardWithdrawalRepository,
		RewardRepository rewardRepository) {
		this.rewardWithdrawalRepository = rewardWithdrawalRepository;
		this.rewardRepository = rewardRepository;
	}

	@Transactional
	public void withdrawReward(Long userId, String bankCodeStd, String bankName, Integer withdrawalAmount,
		String accountNum, String accountHolderName) {

		// 현재 리워드 정보 조회
		Optional<Reward> rewardOptional = rewardRepository.findRewardsByUserId(userId);
		if (rewardOptional.isEmpty()) {
			throw new IllegalArgumentException("해당 사용자에 대한 리워드 정보를 찾을 수 없습니다.");
		}

		Reward reward = rewardOptional.get();
		Integer currentRewardAmount = reward.getRewardSum();

		// 인출 가능한 최소 리워드는 5000 이상이어야 합니다.
		if (currentRewardAmount < 5000) {
			throw new IllegalArgumentException("현재 인출 가능한 포인트는 5000P 이상입니다.");
		} else if (currentRewardAmount < withdrawalAmount) {
			throw new IllegalArgumentException("현재 인출 가능한 포인트는 " + currentRewardAmount + "P 입니다.");
		}

		// 인출 로직
		RewardWithdrawal rewardWithdrawal = new RewardWithdrawal();
		rewardWithdrawal.setUser(reward.getUser());
		rewardWithdrawal.setBankCodeStd(bankCodeStd);
		rewardWithdrawal.setBankName(bankName);
		rewardWithdrawal.setRewardWithdrawalAmount(withdrawalAmount);
		rewardWithdrawal.setAccountNum(accountNum);
		rewardWithdrawal.setAccountHolderName(accountHolderName);
		rewardWithdrawal.setWithdrawn(true);
		rewardWithdrawal.setLastUsedDate(LocalDateTime.now());

		// 리워드 인출 기록 저장
		rewardWithdrawalRepository.save(rewardWithdrawal);

		// WITHDRAWAL 리워드 생성 및 저장
		Reward newReward = new Reward();
		newReward.setUser(reward.getUser());
		newReward.setCreatedAt(LocalDateTime.now());
		newReward.setPreviousRewardSum(reward.getRewardSum());
		newReward.setReceivedRewardAmount(0);
		newReward.setRewardSum(reward.getRewardSum() - withdrawalAmount);
		newReward.setRewardType(RewardType.WITHDRAWAL);
		newReward.setSpentRewardAmount(withdrawalAmount);
		newReward.setRewardWithdrawalAccount(reward.getRewardWithdrawalAccount());
		rewardRepository.save(newReward);
	}

	public List<WithdrawalHistoryDto> getWithdrawalHistory(Long userId, int page, int pageSize) {
		PageRequest pageRequest = PageRequest.of(page, pageSize);
		return rewardWithdrawalRepository.findByUserIdOrderByLastUsedDateDesc(userId, pageRequest)
			.stream()
			.map(this::convertToDto)
			.collect(Collectors.toList());
	}

	private WithdrawalHistoryDto convertToDto(RewardWithdrawal rewardWithdrawal) {
		WithdrawalHistoryDto dto = new WithdrawalHistoryDto();
		dto.setRewardWithdrawalAmount(rewardWithdrawal.getRewardWithdrawalAmount());
		dto.setLastUsedDate(rewardWithdrawal.getLastUsedDate());
		return dto;
	}
}
