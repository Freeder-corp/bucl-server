package com.freeder.buclserver.app.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.affiliates.service.AffiliateService;
import com.freeder.buclserver.app.orders.OrdersService;
import com.freeder.buclserver.app.user.service.UserService;
import com.freeder.buclserver.core.security.CustomUserDetails;
import com.freeder.buclserver.domain.user.dto.response.MyAffiliateResponse;
import com.freeder.buclserver.domain.user.dto.response.MyOrderDetailResponse;
import com.freeder.buclserver.domain.user.dto.response.MyOrderResponse;
import com.freeder.buclserver.domain.user.dto.response.MyProfileResponse;
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
	private final OrdersService ordersService;

	@GetMapping("/v1/my/profile")
	public BaseResponse getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		MyProfileResponse myProfile = userService.getMyProfile(userId);
		return new BaseResponse(myProfile, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/v1/my/profile/affiliates")
	public BaseResponse getMyAffiliates(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		List<MyAffiliateResponse> myAffiliateList = affiliateService.getMyAffiliates(userId);
		return new BaseResponse(myAffiliateList, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/v1/my/profile/orders")
	public BaseResponse getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		List<MyOrderResponse> myOrderList = ordersService.getMyOrders(userId);
		return new BaseResponse(myOrderList, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/v1/my/profile/orders/{consumerOrderId}")
	public BaseResponse getMyOrderDetail(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long consumerOrderId
	) {
		Long userId = Long.valueOf(userDetails.getUserId());
		MyOrderDetailResponse myOrderDetail = ordersService.getMyOrderDetail(userId, consumerOrderId);
		return new BaseResponse(myOrderDetail, HttpStatus.OK, "요청 성공");
	}
}
