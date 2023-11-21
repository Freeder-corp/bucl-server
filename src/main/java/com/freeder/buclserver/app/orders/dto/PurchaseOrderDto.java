package com.freeder.buclserver.app.orders.dto;

import com.freeder.buclserver.domain.consumerpurchaseorder.entity.ConsumerPurchaseOrder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PurchaseOrderDto {
	//주문 상세보기 ->
	//주문 날짜
	//가격/수량
	//배송현황
	//구매확정
	//구매후기 작성
	// 상품사진
	// 브랜드명, 상품명
	private String productOrderCode;
	private String productOptionValue;
	private int productAmount;
	private int productOrderQty;
	private int productOrderAmount;

	public static PurchaseOrderDto from(ConsumerPurchaseOrder consumerPurchaseOrder) {
		return new PurchaseOrderDto(
			consumerPurchaseOrder.getProductOrderCode(),
			consumerPurchaseOrder.getProductOptionValue(),
			consumerPurchaseOrder.getProductAmount(),
			consumerPurchaseOrder.getProductOrderQty(),
			consumerPurchaseOrder.getProductOrderAmount()
		);
	}
}
