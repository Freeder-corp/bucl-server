package com.freeder.buclserver.app.payment.dto;

import lombok.Getter;

@Getter
public class PaymentVerifyDto {
	private String impUid;
	private int amount;
}
