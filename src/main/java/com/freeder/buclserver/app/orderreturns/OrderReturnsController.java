package com.freeder.buclserver.app.orderreturns;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.orderreturns.vo.OrdReturnReqDto;
import com.freeder.buclserver.app.orderreturns.vo.OrdReturnRespDto;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/order-returns")
@Tag(name = "order-returns 관련 API", description = "주문 반품 관련 API")
public class OrderReturnsController {
	private String testSocialId = "3195839289"; //"3895839289";
	private final OrderReturnsService orderReturnsService;

	@PostMapping(path = "/{order_code}/approval")
	public BaseResponse<OrdReturnRespDto> addOrderReturnApproval(
		@PathVariable(name = "order_code") String orderCode,
		@RequestBody OrdReturnReqDto ordReturnReqDto
	) {
		OrdReturnRespDto ordReturnRespDto = orderReturnsService.createOrderReturnApproval(testSocialId, orderCode,
			ordReturnReqDto);
		return new BaseResponse<>(ordReturnRespDto, HttpStatus.CREATED, orderCode + " 에 대해서 주문 반품 승인됐습니다.");
	}

	// @PutMapping(path = "/approval/{order_code}")
	// public BaseResponse<String> modifyOrderCancelApproval(@PathVariable(name = "order_code") String orderCode) {
	// 	return new BaseResponse<>("ㅋㅋㅋㅋㅋ", HttpStatus.CREATED, "주문 취소 됐습니다.");
	// }
}
