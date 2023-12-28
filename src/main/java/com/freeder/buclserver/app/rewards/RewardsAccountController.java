package com.freeder.buclserver.app.rewards;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	public RewardsAccountController(OpenBankingService openBankingService) {
		this.openBankingService = openBankingService;
	}

	@GetMapping("/certification")
	public BaseResponse<?> requestOpenApiUserCertification() {
		try {
			openBankingService.requestOpenApiUserCertification();
			return new BaseResponse<>(null, HttpStatus.OK, "Success");
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
		}
	}

	@PostMapping("/token")
	public BaseResponse<OpenBankingAccessTokenDto> requestOpenApiAccessToken(@RequestParam String code) {
		try {
			OpenBankingAccessTokenDto accessToken = openBankingService.requestOpenApiAccessToken(code);
			return new BaseResponse<>(accessToken, HttpStatus.OK, "Success");
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
		}
	}

}
