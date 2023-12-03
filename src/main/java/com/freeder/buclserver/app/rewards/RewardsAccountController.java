package com.freeder.buclserver.app.rewards;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/openapi")
@Tag(name = "account API", description = "금융결제원 Open API")
public class RewardsAccountController {

	private final OpenApiService openApiService;

	public RewardsAccountController(OpenApiService openApiService) {
		this.openApiService = openApiService;
	}

	@PostMapping("/token")
	public BaseResponse<?> requestOpenApiAccessToken() {
		try {
			openApiService.requestOpenApiAccessToken();
			return new BaseResponse<>(null, HttpStatus.OK, "Success");
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
		}
	}

}
