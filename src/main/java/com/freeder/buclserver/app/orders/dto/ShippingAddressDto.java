package com.freeder.buclserver.app.orders.dto;

import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShippingAddressDto {
	// 배송지
	private String recipientNam;
	private String contactNumber;
	private String zipCode;
	private String address;
	private String addressDetail;

	public static ShippingAddressDto from(ShippingAddress shippingAddress) {
		return new ShippingAddressDto(
			shippingAddress.getRecipientName(),
			shippingAddress.getContactNumber(),
			shippingAddress.getZipCode(),
			shippingAddress.getAddress(),
			shippingAddress.getAddressDetail()
		);
	}
}
