package com.freeder.buclserver.app.orders;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.orders.dto.OrderDetailDto;
import com.freeder.buclserver.app.orders.dto.OrderDto;
import com.freeder.buclserver.app.orders.dto.ShpAddrDto;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/orders")
@Tag(name = "orders 관련 API", description = "주문 관련 API")
public class OrdersController {
	private Long userId = 1L;

	private final OrdersService ordersService;

	@GetMapping("/{orderCode}")
	public BaseResponse<OrderDetailDto> findOrderDetail(
		@PathVariable("orderCode") String orderCode) {
		OrderDetailDto orderDetailDto = ordersService.readOrderDetail(userId, orderCode);
		return new BaseResponse<>(orderDetailDto, HttpStatus.OK, "주문 " + orderCode + " 상세보기");
	}

	@GetMapping("")
	public BaseResponse<List<OrderDto>> findOrderList(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int pageSize) {

		Pageable pageable = PageRequest.of(page, pageSize);
		List<OrderDto> consumerOrders = ordersService.readOrderList(userId, pageable);
		return new BaseResponse<>(consumerOrders, HttpStatus.OK, "주문 리스트 가져왔습니다.");
	}

	@PutMapping("/{orderCode}/address")
	public BaseResponse<ShpAddrDto> modifyOrderShpAddr(
		@PathVariable("orderCode") String orderCode,
		@RequestBody ShpAddrDto shpAddrDto) {
		ShpAddrDto shpAddr = ordersService.updateOrderShpAddr(userId, orderCode, shpAddrDto);
		return new BaseResponse<>(shpAddr, HttpStatus.OK, "배송지 정보가 수정 되었습니다.");
	}

	@PutMapping("/{orderCode}/confirmation")
	public BaseResponse<String> modifyOrderConfirmation(
		@PathVariable("orderCode") String orderCode) {
		ordersService.updateOrderConfirmation(userId, orderCode);
		return new BaseResponse<>(orderCode, HttpStatus.OK, orderCode + " 주문 확정 되었습니다.");
	}

	// @GetMapping("/tracking/{tracking_number}")
	// public BaseResponse<List<TrackingDetailDto>> findTrackingNumber(
	// 	@PathVariable("tracking_number") String trackingNumber) throws JsonProcessingException {
	// 	List<TrackingDetailDto> trackingInfoDto = ordersService.readTrackingInfo(trackingNumber);
	// 	return new BaseResponse<>(
	// 		trackingInfoDto, HttpStatus.OK, "택배 운송 정보 가져왔습니다."
	// 	);
	// }
}
