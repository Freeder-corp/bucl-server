package com.freeder.buclserver.app.orders;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.orders.dto.ConsumerOrderDto;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "orders 관련 API", description = "주문 관련 API")
public class OrdersController {

	private final OrdersService service;
	Long userId = 2L;

	@GetMapping("/purchase/{product_id}")
	public BaseResponse<List<ConsumerOrderDto>> findPurchaseOrderList(
		@PathVariable(name = "product_id") Long productId
	) {
		return service.getOrdersDocument(productId, userId);
	}

	// @PutMapping("/purchase")
	// public BaseResponse<String> modifyPurchaseOrder(@Valid @RequestBody List<PurchaseOrderDto> purchaseOrderDtos) {
	// 	return service.updateOrderPurchase(purchaseOrderDtos, userId);
	// }
	//
	// @PutMapping("/tracking-number")
	// public BaseResponse<String> updateTrackingNum(@Valid @RequestBody List<TrackingNumDto> trackingNumDtos) {
	// 	return service.updateTrackingNum(trackingNumDtos, userId);
	// }

}