package com.freeder.buclserver.domain.usershippingaddress.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record AddressCreateRequest(
	@NotBlank String shippingAddressName,
	@NotBlank String recipientName,
	@NotBlank
	@Pattern(regexp = "^\\d{5}$", message = "올바른 형식의 우편코드가 아닙니다.")
	String zipCode,
	@NotBlank String address,
	@NotBlank String addressDetail,
	@NotBlank
	@Pattern(regexp = "^(\\d{3}-\\d{4}-\\d{4}|\\d{3}-\\d{3}-\\d{4})$", message = "올바른 전화번호 형식이 아닙니다.")
	String contactNumber,
	@NonNull boolean isDefaultAddress
) {
}
