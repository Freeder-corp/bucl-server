package com.freeder.buclserver.app.ordercancels;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.ordercancels.dto.OrderCancelResponseDto;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/order-cancels")
@Tag(name = "order-cancels 관련 API", description = "주문취소 관련 API")
public class OrderCancelsController {

	private Long userId = 1L;
	private final OrderCancelsService orderCancelsService;

	@PostMapping(path = "/{order_code}")
	public BaseResponse<OrderCancelResponseDto> addOrderCancel(
		@PathVariable(name = "order_code") String orderCode
	) {
		OrderCancelResponseDto orderCancelResponseDto = orderCancelsService.createOrderCancel(userId, orderCode);
		return new BaseResponse<>(orderCancelResponseDto, HttpStatus.CREATED, "주문 취소 됐습니다.");
	}

	@PutMapping(path = "/{order_code}/approval")
	public BaseResponse<String> modifyOrderCancelApproval(@PathVariable(name = "order_code") String orderCode) {
		orderCancelsService.updateOrderCancelApproval(userId, orderCode);
		return new BaseResponse<>(orderCode, HttpStatus.OK, orderCode + " 주문 취소 승인 완료했습니다.");
	}
}
