package com.freeder.buclserver.domain.consumerorder.vo;

public enum OrderStatus {
    ORDERED, //주문됨
    ORDER_CANCELED,//주문 취소됨
    ORDER_EXCHANGED, //교환됨
    ORDER_RETURN // 반품됨
}
