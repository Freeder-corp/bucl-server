package com.freeder.buclserver.app.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.affiliates.service.AffiliateService;
import com.freeder.buclserver.app.user.service.UserService;
import com.freeder.buclserver.core.security.CustomUserDetails;
import com.freeder.buclserver.domain.user.dto.response.UserAffiliateResponse;
import com.freeder.buclserver.domain.user.dto.response.UserProfileResponse;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
@Tag(name = "user API", description = "회원 관련 API")
public class UserController {

	private final UserService userService;
	private final AffiliateService affiliateService;

	@GetMapping("/v1/my/profile")
	public BaseResponse getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String userId = userDetails.getUserId();
		UserProfileResponse userProfile = userService.getMyProfile(Long.valueOf(userId));
		return new BaseResponse(userProfile, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/v1/my/profile/affiliates")
	public BaseResponse getMyAffiliates(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String userId = userDetails.getUserId();
		List<UserAffiliateResponse> userAffiliateList = affiliateService.getMyAffiliates(Long.valueOf(userId));
		return new BaseResponse(userAffiliateList, HttpStatus.OK, "요청 성공");
	}
}
