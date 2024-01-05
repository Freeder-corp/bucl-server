package com.freeder.buclserver.app.rewards;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.domain.openbanking.dto.OpenBankingAccessTokenDto;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/openapi")
@Tag(name = "account API", description = "금융결제원 Open API")
public class RewardsAccountController {

	private final OpenBankingService openBankingService;
	private final RewardsWithdrawalAccountService rewardsWithdrawalAccountService;

	public RewardsAccountController(OpenBankingService openBankingService,
		RewardsWithdrawalAccountService rewardsWithdrawalAccountService) {
		this.openBankingService = openBankingService;
		this.rewardsWithdrawalAccountService = rewardsWithdrawalAccountService;
	}

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

}
