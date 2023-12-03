package com.freeder.buclserver.app.my.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.my.service.AddressService;
import com.freeder.buclserver.app.my.service.MyService;
import com.freeder.buclserver.core.security.CustomUserDetails;
import com.freeder.buclserver.domain.user.dto.response.MyAffiliateResponse;
import com.freeder.buclserver.domain.user.dto.response.MyOrderDetailResponse;
import com.freeder.buclserver.domain.user.dto.response.MyOrderResponse;
import com.freeder.buclserver.domain.user.dto.response.MyProfileResponse;
import com.freeder.buclserver.domain.usershippingaddress.dto.UserShippingAddressDto;
import com.freeder.buclserver.domain.usershippingaddress.dto.request.AddressCreateRequest;
import com.freeder.buclserver.domain.usershippingaddress.dto.response.AddressCreateResponse;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/my")
@Tag(name = "my 관련 API", description = "마이페이지 관련 API")
public class MyController {

	private final MyService myService;
	private final AddressService addressService;

	@GetMapping("/profile")
	public BaseResponse getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		MyProfileResponse myProfile = myService.getMyProfile(userId);
		return new BaseResponse(myProfile, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/profile/affiliates")
	public BaseResponse getMyAffiliates(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		List<MyAffiliateResponse> myAffiliateList = myService.getMyAffiliates(userId);
		return new BaseResponse(myAffiliateList, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/profile/orders")
	public BaseResponse getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		List<MyOrderResponse> myOrderList = myService.getMyOrders(userId);
		return new BaseResponse(myOrderList, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/profile/orders/{consumerOrderId}")
	public BaseResponse getMyOrderDetail(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long consumerOrderId
	) {
		Long userId = Long.valueOf(userDetails.getUserId());
		MyOrderDetailResponse myOrderDetail = myService.getMyOrderDetail(userId, consumerOrderId);
		return new BaseResponse(myOrderDetail, HttpStatus.OK, "요청 성공");
	}

	@PostMapping("/addresses")
	public BaseResponse createMyAddress(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody AddressCreateRequest addressCreateRequest
	) {
		Long userId = Long.valueOf(userDetails.getUserId());
		AddressCreateResponse addressCreateResponse = addressService.createMyAddress(userId, addressCreateRequest);
		return new BaseResponse(addressCreateResponse, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/addresses")
	public BaseResponse getMyAddressList(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		List<UserShippingAddressDto> addressList = addressService.getMyAddressList(userId);
		return new BaseResponse(addressList, HttpStatus.OK, "요청 성공");
	}

	@DeleteMapping("/addresses/{addressId}")
	public BaseResponse deleteMyAddress(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long addressId
	) {
		Long userId = Long.valueOf(userDetails.getUserId());
		addressService.deleteMyAddress(userId, addressId);
		return new BaseResponse(addressId, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/address/default")
	public BaseResponse getMyDefaultAddress(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = Long.valueOf(userDetails.getUserId());
		UserShippingAddressDto defaultAddress = addressService.getMyDefaultAddress(userId);
		return new BaseResponse(defaultAddress, HttpStatus.OK, "요청 성공");
	}
}
