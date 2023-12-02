package com.freeder.buclserver.domain.usershippingaddress.dto.request;

import javax.validation.constraints.NotBlank;

public record AddressCreateRequest(
	@NotBlank String shippingAddressName,
	@NotBlank String recipientName,
	@NotBlank String zipCode,
	@NotBlank String address,
	@NotBlank String addressDetail,
	String contactNumber,
	boolean isDefaultAddress
) {
}
