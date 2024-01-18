package com.freeder.buclserver.app.rewards;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.core.security.CustomUserDetails;
import com.freeder.buclserver.domain.reward.dto.RewardDto;
import com.freeder.buclserver.domain.rewardwithdrawal.dto.WithdrawalDto;
import com.freeder.buclserver.domain.rewardwithdrawal.dto.WithdrawalRequestDto;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/rewards")
@Tag(name = "rewards API", description = "적립금 관련 API")
public class RewardsController {

	private final RewardsService rewardsService;
	private final RewardsWithdrawalService rewardsWithdrawalService;

	@Autowired
	public RewardsController(RewardsService rewardsService, RewardsWithdrawalService rewardsWithdrawalService) {
		this.rewardsService = rewardsService;
		this.rewardsWithdrawalService = rewardsWithdrawalService;
	}

	@GetMapping("/crnt-amt")
	@Transactional(readOnly = true)
	public BaseResponse<Integer> getUserRewards(
		// @AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = 1L;
		int currentRewardAmount = rewardsService.getUserRewardCrntAmount(userId);

		return new BaseResponse<>(currentRewardAmount, HttpStatus.OK, "리워드 조회 성공");
	}

	@GetMapping("")
	@Transactional(readOnly = true)
	public BaseResponse<List<RewardDto>> getRewardHistory(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "5") int pageSize
		// @AuthenticationPrincipal CustomUserDetails userDetails
	) {
		// Long userId = getUserIdFromUserDetails(userDetails);
		Long userId = 1L;
		List<RewardDto> rewardHistory = rewardsService.getRewardHistoryPageable(userId, page, pageSize);

		return new BaseResponse<>(rewardHistory, HttpStatus.OK, "적립금 내역 조회 성공");
	}

	private Long getUserIdFromUserDetails(CustomUserDetails userDetails) {
		return Long.parseLong(userDetails.getUserId());
	}

	@PostMapping("/withdrawals")
	@Transactional
	public BaseResponse<WithdrawalDto> withdrawReward(
		// @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody WithdrawalRequestDto withdrawalRequestDto
	) {
		// Long userId = getUserIdFromUserDetails(userDetails);
		Long userId = 1L;
		WithdrawalDto withdrawalDto = rewardsWithdrawalService.withdrawReward(
			userId,
			withdrawalRequestDto.getBankCodeStd(),
			withdrawalRequestDto.getBankName(),
			withdrawalRequestDto.getWithdrawalAmount(),
			withdrawalRequestDto.getAccountNum(),
			withdrawalRequestDto.getAccountHolderName()
		);

		return new BaseResponse<>(withdrawalDto, HttpStatus.OK, "리워드 인출 성공");
	}

	@GetMapping("/withdrawals")
	@Transactional(readOnly = true)
	public BaseResponse<List<WithdrawalDto>> getWithdrawalHistory(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int pageSize
		// @AuthenticationPrincipal CustomUserDetails userDetails,
	) {
		// Long userId = getUserIdFromUserDetails(userDetails);
		Long userId = 1L;
		List<WithdrawalDto> withdrawalHistory = rewardsWithdrawalService.getWithdrawalHistory(userId, page,
			pageSize);

		return new BaseResponse<>(withdrawalHistory, HttpStatus.OK, "인출내역 조회 성공");
	}

	@GetMapping("/account")
	public BaseResponse<WithdrawalAccountResponseDto> testResponseWithdrawalAccount() {
		WithdrawalAccountResponseDto test = new WithdrawalAccountResponseDto();
		test.setAccountNum("5560910215263");
		test.setBankName("농협");
		test.setBankCode("09");
		test.setAccountHolderName("김창민");

		return new BaseResponse<>(test, HttpStatus.OK, "인출계좌 조회 성공");
	}

	@Getter
	@Setter
	public class WithdrawalAccountResponseDto {
		private String bankName;
		private String bankCode;
		private String accountNum;
		private String accountHolderName;

	}
}