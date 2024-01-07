package com.freeder.buclserver.app.orders.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrackingNumDto {
	@NotEmpty
	@NotNull
	private String orderCode;
	@NotEmpty
	@NotNull
	private String trackingNum;
	@NotEmpty
	@NotNull
	private String shippingCoName;

}