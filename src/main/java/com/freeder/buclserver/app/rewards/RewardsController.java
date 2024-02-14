package com.freeder.buclserver.app.rewards;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.rewards.dto.PutWithdrawalAccountReq;
import com.freeder.buclserver.core.security.CustomUserDetails;
import com.freeder.buclserver.domain.openbanking.vo.BANK_CODE;
import com.freeder.buclserver.domain.reward.dto.RewardDto;
import com.freeder.buclserver.domain.rewardwithdrawal.dto.WithdrawalDto;
import com.freeder.buclserver.domain.rewardwithdrawal.dto.WithdrawalRequestDto;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.dto.WithdrawalAccountResponseDto;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/rewards")
@RequiredArgsConstructor
@Tag(name = "rewards API", description = "적립금 관련 API")
public class RewardsController {

	private final RewardsService rewardsService;
	private final RewardsWithdrawalService rewardsWithdrawalService;
	private final RewardsWithdrawalAccountService rewardsWithdrawalAccountService;

	@GetMapping("/crnt-amt")
	@Transactional(readOnly = true)
	public BaseResponse<Integer> getUserRewards(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = getUserIdFromUserDetails(userDetails);
		int currentRewardAmount = rewardsService.getUserRewardCrntAmount(userId);

		return new BaseResponse<>(currentRewardAmount, HttpStatus.OK, "리워드 조회 성공");
	}

	@GetMapping("")
	@Transactional(readOnly = true)
	public BaseResponse<List<RewardDto>> getRewardHistory(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "5") int pageSize,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = getUserIdFromUserDetails(userDetails);
		List<RewardDto> rewardHistory = rewardsService.getRewardHistoryPageable(userId, page, pageSize);

		return new BaseResponse<>(rewardHistory, HttpStatus.OK, "적립금 내역 조회 성공");
	}

	@PostMapping("/withdrawals")
	@Transactional
	public BaseResponse<WithdrawalDto> withdrawReward(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody WithdrawalRequestDto withdrawalRequestDto
	) {
		Long userId = getUserIdFromUserDetails(userDetails);
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
		@RequestParam(defaultValue = "10") int pageSize,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = getUserIdFromUserDetails(userDetails);
		List<WithdrawalDto> withdrawalHistory = rewardsWithdrawalService.getWithdrawalHistory(userId, page,
			pageSize);

		return new BaseResponse<>(withdrawalHistory, HttpStatus.OK, "인출내역 조회 성공");
	}

	@GetMapping("/account")
	public BaseResponse<WithdrawalAccountResponseDto> getWithdrawalAccount(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());

		return new BaseResponse<>(rewardsWithdrawalAccountService.getRewardAccount(userId), HttpStatus.OK,
			"인출계좌 조회 성공");
	}

	@PutMapping("/account")
	public BaseResponse<Boolean> putWithdrawalAccount(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody PutWithdrawalAccountReq putWithdrawalAccountReq

	) {
		Long userId = Long.valueOf(userDetails.getUserId());

		Boolean isCreatedAccount = rewardsWithdrawalAccountService.saveWithdrawalAccount(userId,
			putWithdrawalAccountReq.getBankName(),
			putWithdrawalAccountReq.getAccountNum());
		return new BaseResponse<>(isCreatedAccount, HttpStatus.OK, "인출계좌 수정 완료");
	}

	@GetMapping("/bank-list")
	public BaseResponse<List<BANK_CODE>> getWithdrawalAccount(
	) {
		List<BANK_CODE> bankList = Arrays.asList(BANK_CODE.values());
		return new BaseResponse<>(bankList, HttpStatus.OK, "인출계좌 수정 완료");
	}

	private Long getUserIdFromUserDetails(CustomUserDetails userDetails) {
		return Long.valueOf(userDetails.getUserId());
	}
}