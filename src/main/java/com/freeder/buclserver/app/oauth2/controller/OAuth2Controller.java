package com.freeder.buclserver.app.oauth2.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.oauth2.dto.request.KakaoLoginRequest;
import com.freeder.buclserver.app.oauth2.dto.request.RefreshTokenRequest;
import com.freeder.buclserver.app.oauth2.dto.response.KakaoUserInfoResponse;
import com.freeder.buclserver.app.oauth2.dto.response.TokenResponse;
import com.freeder.buclserver.app.oauth2.service.JwtTokenService;
import com.freeder.buclserver.app.user.UserService;
import com.freeder.buclserver.domain.user.dto.UserDto;
import com.freeder.buclserver.domain.user.vo.JoinType;
import com.freeder.buclserver.global.openfeign.kakao.KakaoApiClient;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
@Tag(name = "oauth2 API", description = "소셜 관련 API")
public class OAuth2Controller {

	private final JwtTokenService jwtTokenService;
	private final UserService userService;
	private final KakaoApiClient kakaoApiClient;

	@PostMapping("/v1/auth/login/kakao")
	public ResponseEntity<TokenResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {

		KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo("Bearer " + request.kakaoAccessToken());
		UserDto userDto = userService.findBySocialUid(userInfo.getId())
			.orElseGet(() -> userService.join(userInfo.toUserDto()));

		if (userDto.deletedAt() != null) {
			userService.rejoin(userDto.id());
		}

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(jwtTokenService.createJwtTokens(userDto.id(), JoinType.KAKAO));
	}

	@PostMapping("/v1/auth/renewal/tokens")
	public ResponseEntity<TokenResponse> renewTokens(@Valid @RequestBody RefreshTokenRequest request) {
		TokenResponse tokens = jwtTokenService.renewTokens(request.refreshToken());

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(tokens);
	}
}
