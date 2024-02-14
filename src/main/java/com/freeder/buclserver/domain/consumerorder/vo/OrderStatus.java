package com.freeder.buclserver.domain.consumerorder.vo;

public enum OrderStatus {
	ORDERED, //주문 발주 처리 안됨

	ORDERED_PROCESSING,// 주문됨, 발주 넣음
	ORDERED_IN_DELIVERY, //주문됨, 배송 중
	ORDERED_DELIVERED, // 주문됨, 배송 완료
	ORDERED_DELAY, // 주문됨, 배송 지연

	ORDER_CANCELING, //주문 취소 처리중
	ORDER_CANCELED, //주문 취소됨
	ORDER_EXCHANGING, //교환 처리중
	ORDER_EXCHANGED, //교환됨
	ORDER_RETURNING, // 반품 처리 중
	ORDER_RETURNED // 반품됨
}

