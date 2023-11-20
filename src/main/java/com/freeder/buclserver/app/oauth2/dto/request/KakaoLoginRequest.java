package com.freeder.buclserver.app.oauth2.dto.request;

import javax.validation.constraints.NotBlank;

public record KakaoLoginRequest(@NotBlank String kakaoAccessToken) {
}
