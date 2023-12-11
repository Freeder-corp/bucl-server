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
import com.freeder.buclserver.domain.rewardwithdrawal.vo.WithdrawalStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		try {
			// 현재 리워드 정보 조회
			Optional<Reward> rewardOptional = rewardRepository.findRewardsByUserId(userId);
			if (rewardOptional.isEmpty()) {
				throw new IllegalArgumentException("해당 사용자에 대한 리워드 정보를 찾을 수 없습니다.");
			}

			Reward reward = rewardOptional.get();
			Integer currentRewardAmount = reward.getRewardSum();

			// 인출 가능한 최소 리워드는 5000 이상이어야 합니다.
			if (withdrawalAmount < 5000) {
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
			rewardWithdrawal.setWithdrawalStatus(WithdrawalStatus.WTHDR_WTNG);
			rewardWithdrawal.setWithdrawn(true);
			rewardWithdrawal.setLastUsedDate(LocalDateTime.now());

			// 리워드 인출 기록 저장
			RewardWithdrawal newRewardWithdrawal = rewardWithdrawalRepository.save(rewardWithdrawal);

			// WITHDRAWAL 리워드 생성 및 저장
			Reward newReward = new Reward();
			newReward.setUser(reward.getUser());
			newReward.setCreatedAt(LocalDateTime.now());
			newReward.setPreviousRewardSum(reward.getRewardSum());
			newReward.setReceivedRewardAmount(-1 * withdrawalAmount);
			newReward.setRewardSum(reward.getRewardSum() - withdrawalAmount);
			newReward.setRewardType(RewardType.WITHDRAWAL);
			newReward.setSpentRewardAmount(withdrawalAmount);
			newReward.setRewardWithdrawal(newRewardWithdrawal);
			rewardRepository.save(newReward);

			log.info("리워드 인출 성공 - userId: {}, withdrawalAmount: {}", userId, withdrawalAmount);
		} catch (Exception e) {
			log.error("리워드 인출 실패 - userId: {}, withdrawalAmount: {}", userId, withdrawalAmount, e);
			throw e;
		}
	}

	public List<WithdrawalHistoryDto> getWithdrawalHistory(Long userId, int page, int pageSize) {
		try {
			PageRequest pageRequest = PageRequest.of(page, pageSize);
			List<WithdrawalHistoryDto> withdrawalHistory = rewardWithdrawalRepository.findByUserIdOrderByLastUsedDateDesc(
					userId, pageRequest)
				.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());

			log.info("리워드 인출 내역 조회 성공 - userId: {}, page: {}, pageSize: {}", userId, page, pageSize);
			return withdrawalHistory;
		} catch (Exception e) {
			log.error("리워드 인출 내역 조회 실패 - userId: {}, page: {}, pageSize: {}", userId, page, pageSize, e);
			throw e;
		}
	}

	private WithdrawalHistoryDto convertToDto(RewardWithdrawal rewardWithdrawal) {
		WithdrawalHistoryDto dto = new WithdrawalHistoryDto();
		dto.setRewardWithdrawalAmount(rewardWithdrawal.getRewardWithdrawalAmount());
		dto.setWithdrawalStatus(rewardWithdrawal.getWithdrawalStatus());
		dto.setLastUsedDate(rewardWithdrawal.getLastUsedDate());
		return dto;
	}
}
