package com.freeder.buclserver.app.auth.dto.request;

import javax.validation.constraints.NotBlank;

public record KakaoLoginRequest(@NotBlank String kakaoAccessToken) {
}
