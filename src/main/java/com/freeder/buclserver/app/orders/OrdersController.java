package com.freeder.buclserver.app.orders;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.orders.dto.OrderDto;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/orders")
@Tag(name = "orders 관련 API", description = "주문 관련 API")
public class OrdersController {
	private String testSocialId = "sjfdlkwjlkj149202";

	private final OrdersService ordersService;

	@GetMapping("")
	public BaseResponse<List<OrderDto>> findOrderList(Pageable pageable) {
		List<OrderDto> consumerOrders = ordersService.readOrderList(testSocialId, pageable);
		return new BaseResponse<>(consumerOrders, HttpStatus.OK, "주문 리스트 가져왔습니다.");
	}
}
