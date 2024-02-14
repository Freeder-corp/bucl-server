package com.freeder.buclserver.rewards;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import com.freeder.buclserver.app.rewards.RewardsWithdrawalService;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.reward.vo.RewardType;
import com.freeder.buclserver.domain.rewardwithdrawal.dto.WithdrawalDto;
import com.freeder.buclserver.domain.rewardwithdrawal.entity.RewardWithdrawal;
import com.freeder.buclserver.domain.rewardwithdrawal.repository.RewardWithdrawalRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.util.UserTestUtil;

@DisplayName("리워드 인출 / 인출 내역 조회 API")
@ExtendWith(MockitoExtension.class)
public class RewardsWithdrawalServiceTest {

	@Mock
	private RewardRepository rewardRepository;

	@Mock
	private RewardWithdrawalRepository rewardWithdrawalRepository;

	@InjectMocks
	private RewardsWithdrawalService rewardsWithdrawalService;

	@Test
	@DisplayName("리워드 인출 - 성공")
	void 리워드인출_성공테스트() {
		// Given
		Long userId = 1L;
		Reward mockReward = Reward.builder()
			.id(1L)
			.user(UserTestUtil.createWthdrawalUser())
			.product(new Product())
			.rewardType(RewardType.WITHDRAWAL)
			.receivedRewardAmount(10000)
			.spentRewardAmount(5000)
			.previousRewardSum(15000)
			.rewardSum(10000)
			.build();

		when(rewardRepository.findRewardsByUserId(userId)).thenReturn(Optional.of(mockReward));

		// When
		rewardsWithdrawalService.withdrawReward(userId, "은행코드", "은행명", 5000, "계좌번호", "예금주명");

		// Then
		verify(rewardWithdrawalRepository, times(1)).save(any(RewardWithdrawal.class));
		verify(rewardRepository, times(1)).save(any(Reward.class));
	}

	@Test
	@DisplayName("리워드 인출 - 실패 (인출 금액 부족)")
	void 리워드인출_실패테스트() {
		// Given
		Long userId = 1L;
		Reward mockReward = Reward.builder()
			.id(1L)
			.user(UserTestUtil.createWthdrawalUser())
			.product(new Product())
			.rewardType(RewardType.WITHDRAWAL)
			.receivedRewardAmount(10000)
			.spentRewardAmount(5000)
			.previousRewardSum(15000)
			.rewardSum(10000)
			.build();
		when(rewardRepository.findRewardsByUserId(userId)).thenReturn(Optional.of(mockReward));

		// When & Then
		assertThrows(BaseException.class, () ->
			rewardsWithdrawalService.withdrawReward(userId, "은행코드", "은행명", 50000, "계좌번호", "예금주명"));
	}

	@Test
	@DisplayName("리워드 인출 - 실패 (데이터베이스 액세스 예외)")
	void 리워드인출_데이터베이스실패테스트() {
		// Given
		Long userId = 1L;
		when(rewardRepository.findRewardsByUserId(userId)).thenThrow(new DataAccessException("Data access exception") {
		});
		// When & Then
		assertThrows(BaseException.class, () ->
			rewardsWithdrawalService.withdrawReward(userId, "은행코드", "은행명", 5000, "계좌번호", "예금주명"));
	}

	@Test
	@DisplayName("리워드 인출 내역 조회 - 성공")
	void 리워드인출내역조회_성공테스트() {
		// Given
		Long userId = 1L;
		int page = 0;
		int pageSize = 5;
		List<RewardWithdrawal> mockWithdrawalList = Arrays.asList(
			new RewardWithdrawal(),
			new RewardWithdrawal()
		);
		when(rewardWithdrawalRepository.findByUserIdOrderByLastUsedDateDesc(eq(userId), any()))
			.thenReturn(mockWithdrawalList);

		// When
		List<WithdrawalDto> withdrawalHistory = rewardsWithdrawalService.getWithdrawalHistory(userId, page,
			pageSize);

		// Then
		assertNotNull(withdrawalHistory);
		assertFalse(withdrawalHistory.isEmpty());
	}

	@Test
	@DisplayName("리워드 인출 내역 조회 - 실패 (데이터베이스 액세스 예외)")
	void 리워드인출내역조회_데이터베이스실패테스트() {
		// Given
		Long userId = 1L;
		int page = 0;
		int pageSize = 5;
		when(rewardWithdrawalRepository.findByUserIdOrderByLastUsedDateDesc(eq(userId), any()))
			.thenThrow(new DataAccessException("Data access exception") {
			});

		// When & Then
		assertThrows(BaseException.class, () ->
			rewardsWithdrawalService.getWithdrawalHistory(userId, page, pageSize));
	}
}
