package com.freeder.buclserver.app.auth.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.auth.dto.request.KakaoLoginRequest;
import com.freeder.buclserver.app.auth.dto.request.RefreshTokenRequest;
import com.freeder.buclserver.app.auth.dto.response.KakaoUserInfoResponse;
import com.freeder.buclserver.app.auth.dto.response.TokenResponse;
import com.freeder.buclserver.app.auth.service.JwtTokenService;
import com.freeder.buclserver.app.my.service.MyService;
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

	private final JwtTokenService jwtTokenService;
	private final KakaoApiClient kakaoApiClient;
	private final MyService myService;

	// TODO: 카카오 토큰을 헤더로 받을지 DTO로 받을지 프론트와 의논 필요
	@PostMapping("/login/kakao")
	public BaseResponse kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
		KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo("Bearer " + request.kakaoAccessToken());

		UserDto userDto = myService.findBySocialIdAndDeletedAtIsNull(userInfo.getId())
			.orElseGet(() -> myService.join(userInfo.toUserDto()));

		TokenResponse tokens = jwtTokenService.createJwtTokens(userDto.id(), userDto.role());
		return new BaseResponse(tokens, HttpStatus.OK, "요청 성공");
	}

	@PostMapping("/renewal/tokens")
	public BaseResponse renewTokens(@Valid @RequestBody RefreshTokenRequest request) {
		TokenResponse tokens = jwtTokenService.renewTokens(request.refreshToken());
		return new BaseResponse(tokens, HttpStatus.OK, "요청 성공");
	}

	@PostMapping("/logout")
	public BaseResponse logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String userId = userDetails.getUserId();
		myService.deleteRefreshToken(Long.valueOf(userId));
		return new BaseResponse(userId, HttpStatus.OK, "요청 성공");
	}

	@PostMapping("/member-withdrawal")
	public BaseResponse withdrawal(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String userId = userDetails.getUserId();
		myService.withdrawal(Long.valueOf(userId));
		return new BaseResponse(userId, HttpStatus.OK, "요청 성공");
	}
}
