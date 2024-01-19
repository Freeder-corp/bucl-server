package com.freeder.buclserver.app.auth.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.auth.dto.request.KakaoLoginRequest;
import com.freeder.buclserver.app.auth.dto.response.KakaoUserInfoResponse;
import com.freeder.buclserver.app.auth.dto.response.TokenResponse;
import com.freeder.buclserver.app.auth.service.AuthService;
import com.freeder.buclserver.core.security.CustomUserDetails;
import com.freeder.buclserver.domain.user.dto.UserDto;
import com.freeder.buclserver.global.openfeign.kakao.KakaoApiClient;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
@Tag(name = "oauth2 API", description = "소셜 관련 API")
public class AuthController {

	private final AuthService authService;
	private final KakaoApiClient kakaoApiClient;

	@Value("${bucl.service.auth.COOKIE-MAX-AGE-REFRESH_TOKEN}")
	private int COOKIE_MAX_AGE_REFRESH_TOKEN;

	@PostMapping("/login/kakao")
	public BaseResponse kakaoLogin(@Valid @RequestBody KakaoLoginRequest request, HttpServletResponse response) {
		KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo("Bearer " + request.kakaoAccessToken());

		UserDto userDto = authService.findBySocialId(userInfo.getId())
			.orElseGet(() -> authService.join(userInfo.toUserDto()));

		if (userDto.deletedAt() != null) {
			authService.rejoin(userDto.id());
		}

		TokenResponse tokens = authService.createJwtTokens(userDto.id(), userDto.role());

		response.addCookie(createCookie("refresh-token", tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		return new BaseResponse(tokens.accessToken(), HttpStatus.OK, "요청 성공");
	}

	@PostMapping("/renewal/tokens")
	public BaseResponse renewTokens(@RequestHeader("refresh-token") String refreshToken, HttpServletResponse response) {
		TokenResponse tokens = authService.renewTokens(refreshToken);

		response.addCookie(createCookie("refresh-token", tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		return new BaseResponse(tokens.accessToken(), HttpStatus.OK, "요청 성공");
	}

	@PostMapping("/logout")
	public BaseResponse logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String userId = userDetails.getUserId();
		authService.deleteRefreshToken(Long.valueOf(userId));
		return new BaseResponse(userId, HttpStatus.OK, "요청 성공");
	}

	@PostMapping("/member-withdrawal")
	public BaseResponse withdrawal(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String userId = userDetails.getUserId();
		authService.withdrawal(Long.valueOf(userId));
		return new BaseResponse(userId, HttpStatus.OK, "요청 성공");
	}

	private Cookie createCookie(String cookieName, String token, int maxAge) {
		Cookie cookie = new Cookie(cookieName, token);
		cookie.setHttpOnly(true);
		// TODO: https 세팅 후 주석 해제
		// cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		return cookie;
	}
}
