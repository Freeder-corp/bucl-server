package com.freeder.buclserver.domain.openapi.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.freeder.buclserver.domain.openapi.entity.OpenApiAccessToken;

import lombok.Data;

@Data
public class OpenApiAccessTokenDto {
	String access_token;
	String token_type;
	String expires_in;
	String scope;
	String client_use_code;

	public OpenApiAccessToken toEntity() {
		LocalDateTime expireDate = LocalDateTime
			.now(ZoneId.of("Asia/Seoul"))
			.plusSeconds(Integer.parseInt(expires_in));

		return OpenApiAccessToken.builder()
			.accessToken(access_token)
			.tokenType(token_type)
			.expireDate(expireDate.toString())
			.scope(scope)
			.clientUseCode(client_use_code)
			.build();
	}
}
