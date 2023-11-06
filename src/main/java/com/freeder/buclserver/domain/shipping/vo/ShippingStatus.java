package com.freeder.buclserver.domain.shipping.vo;

public enum ShippingStatus {
	PROCESSING, //상품 준비중
	IN_DELIVERY, //배송 중
	DELIVERED, //배송 완료

	DELAY //배송 지연
}
