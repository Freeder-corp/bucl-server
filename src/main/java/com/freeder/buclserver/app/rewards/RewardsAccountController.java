package com.freeder.buclserver.app.rewards;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		try {
			OpenBankingAccessTokenDto accessToken = openBankingService.requestOpenApiAccessToken();
			return new BaseResponse<>(accessToken, HttpStatus.OK, "Success");
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
		}
	}

	@PostMapping("/realname")
	public BaseResponse<Boolean> requestMatchAccountRealName(@RequestBody Map<String, Object> requestBody) {
		Long userId = ((Number)requestBody.get("userId")).longValue();
		String bankCode = (String)requestBody.get("bankCode");
		String bankAccount = (String)requestBody.get("bankAccount");
		String realName = (String)requestBody.get("realName");
		String birthday = (String)requestBody.get("birthday");

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
	public BaseResponse<WithdrawalAccountResponseDto> responseWithdrawalAccount() {
		try {
			Long userId = 11L;
			WithdrawalAccountResponseDto responseDto = rewardsWithdrawalAccountService.getWithdrawalAccountByUserId(
				userId);

			if (responseDto != null) {
				return new BaseResponse<>(responseDto, HttpStatus.OK, "Success");
			} else {
				return new BaseResponse<>(null, HttpStatus.BAD_REQUEST, "에러가 발생했습니다.");
			}
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
		}
	}

}
