package com.freeder.buclserver.app.oauth2.dto.response;

public record TokenResponse(String accessToken, String refreshToken) {

	public static TokenResponse of(String accessToken, String refreshToken) {
		return new TokenResponse(accessToken, refreshToken);
	}
}
