package com.freeder.buclserver.domain.usershippingaddress.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record AddressUpdateRequest(
	@NotBlank String shippingAddressName,
	@NotBlank String recipientName,
	@NotBlank String zipCode,
	@NotBlank String address,
	@NotBlank String addressDetail,
	@NotBlank String contactNumber,
	@NonNull boolean isDefaultAddress
) {
}
