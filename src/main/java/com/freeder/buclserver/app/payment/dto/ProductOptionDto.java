package com.freeder.buclserver.app.payment.dto;

import lombok.Getter;

@Getter
public class ProductOptionDto {
	private String skuCode;
	private int productOrderAmt;
	private int productOrderQty;
	private String productOptVal;
}
