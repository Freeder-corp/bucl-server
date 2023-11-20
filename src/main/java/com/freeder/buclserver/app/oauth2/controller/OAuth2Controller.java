package com.freeder.buclserver.app.oauth2.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.oauth2.dto.request.KakaoLoginRequest;
import com.freeder.buclserver.app.oauth2.dto.response.KakaoUserInfoResponse;
import com.freeder.buclserver.app.oauth2.dto.response.TokenResponse;
import com.freeder.buclserver.app.user.UserService;
import com.freeder.buclserver.core.security.JwtTokenProvider;
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

	private final JwtTokenProvider jwtTokenProvider;
	private final KakaoApiClient kakaoApiClient;
	private final UserService userService;

	@PostMapping("/v1/auth/login/kakao")
	public ResponseEntity<TokenResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest kakaoLoginRequest) {

		KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo("Bearer " + kakaoLoginRequest.kakaoAccessToken());
		UserDto userDto = userService.findBySocialUid(userInfo.getId())
			.orElseGet(() -> userService.join(userInfo.toUserDto()));

		if (userDto.deletedAt() != null) {
			userService.rejoin(userDto.id());
		}

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(TokenResponse.of(jwtTokenProvider.createAccessToken(userDto.id(), JoinType.KAKAO)));
	}
}
