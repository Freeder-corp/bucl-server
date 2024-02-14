package com.freeder.buclserver.app.rewards;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.rewards.dto.PostAccountRealNameReq;
import com.freeder.buclserver.core.security.CustomUserDetails;
import com.freeder.buclserver.domain.openbanking.dto.OpenBankingAccessTokenDto;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.dto.WithdrawalAccountResponseDto;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/openapi")
@Tag(name = "account API", description = "금융결제원 Open API")
public class RewardsAccountController {

	private final OpenBankingService openBankingService;
	private final RewardsWithdrawalAccountService rewardsWithdrawalAccountService;

	@PostMapping("/token")
	public BaseResponse<OpenBankingAccessTokenDto> requestOpenApiAccessToken() {
		OpenBankingAccessTokenDto accessToken = openBankingService.requestOpenApiAccessToken();
		return new BaseResponse<>(accessToken, HttpStatus.OK, "Success");
	}

	@PostMapping("/realname")
	public BaseResponse<Boolean> requestMatchAccountRealName(@RequestBody PostAccountRealNameReq requestBody,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		String bankCode = requestBody.getBankCode();
		String bankAccount = requestBody.getBankAccount();
		String realName = requestBody.getRealName();
		String birthday = requestBody.getBirthday();

		try {
			boolean result = rewardsWithdrawalAccountService.requestMatchAccountRealName(userId, bankCode, bankAccount,
				realName, birthday);
			return new BaseResponse<>(result, HttpStatus.OK, "Success");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
		}
	}

	@GetMapping("/account")
	public BaseResponse<WithdrawalAccountResponseDto> responseWithdrawalAccount(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		WithdrawalAccountResponseDto responseDto = rewardsWithdrawalAccountService.getWithdrawalAccountByUserId(
			userId);

		if (responseDto != null) {
			return new BaseResponse<>(responseDto, HttpStatus.OK, "Success");
		} else {
			return new BaseResponse<>(null, HttpStatus.BAD_REQUEST, "에러가 발생했습니다.");
		}
	}
}
