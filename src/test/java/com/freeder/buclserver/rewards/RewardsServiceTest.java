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
import org.springframework.data.domain.PageRequest;

import com.freeder.buclserver.app.rewards.RewardsService;
import com.freeder.buclserver.domain.reward.dto.RewardDto;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.global.exception.BaseException;

@DisplayName("리워드 / 리워드 내역 조회 API")
@ExtendWith(MockitoExtension.class)
public class RewardsServiceTest {

	@Mock
	private RewardRepository rewardRepository;

	@InjectMocks
	private RewardsService rewardsService;

	@Test
	@DisplayName("리워드 금액 조회 - 성공")
	void 리워드금액조회_성공테스트() {
		// Given
		Long userId = 1L;
		int expectedRewardAmount = 100;
		when(rewardRepository.findFirstByUserId(userId)).thenReturn(Optional.of(expectedRewardAmount));

		// When
		int actualRewardAmount = rewardsService.getUserRewardCrntAmount(userId);

		// Then
		assertEquals(expectedRewardAmount, actualRewardAmount);
	}

	@Test
	@DisplayName("리워드 내역 페이지별 조회 - 성공")
	void getRewardHistoryPageableTest() {
		// Given
		Long userId = 1L;
		int page = 0;
		int pageSize = 5;
		List<Reward> mockRewards = Arrays.asList(
			new Reward(),
			new Reward()
		);
		when(rewardRepository.findByUserIdOrderByCreatedAtDesc(eq(userId), any(PageRequest.class)))
			.thenReturn(Optional.of(mockRewards));

		// When
		List<RewardDto> rewardHistory = rewardsService.getRewardHistoryPageable(userId, page, pageSize);

		// Then
		assertNotNull(rewardHistory);
		assertFalse(rewardHistory.isEmpty());
	}

	@Test
	@DisplayName("리워드 금액 조회 - 데이터 액세스 예외")
	void 리워드금액조회_데이터액세스예외테스트() {
		// Given
		Long userId = 1L;
		when(rewardRepository.findFirstByUserId(userId)).thenThrow(new DataAccessException("Data access exception") {
		});

		// When & Then
		assertThrows(BaseException.class, () -> rewardsService.getUserRewardCrntAmount(userId));
	}

	@Test
	@DisplayName("리워드 내역 페이지별 조회 - 데이터 액세스 예외")
	void 리워드내역조회_데이터액세스예외테스트() {
		// Given
		Long userId = 1L;
		int page = 0;
		int pageSize = 5;
		when(rewardRepository.findByUserIdOrderByCreatedAtDesc(eq(userId), any(PageRequest.class)))
			.thenThrow(new DataAccessException("Data access exception") {
			});

		// When & Then
		assertThrows(BaseException.class, () -> rewardsService.getRewardHistoryPageable(userId, page, pageSize));
	}
}
