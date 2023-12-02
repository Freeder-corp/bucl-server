package com.freeder.buclserver.app.my.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.domain.usershippingaddress.dto.UserShippingAddressDto;
import com.freeder.buclserver.domain.usershippingaddress.dto.request.AddressCreateRequest;
import com.freeder.buclserver.domain.usershippingaddress.dto.response.AddressCreateResponse;
import com.freeder.buclserver.domain.usershippingaddress.entity.UserShippingAddress;
import com.freeder.buclserver.domain.usershippingaddress.repository.UserShippingAddressRepository;
import com.freeder.buclserver.global.exception.user.UserIdNotFoundException;
import com.freeder.buclserver.global.exception.usershippingaddress.AddressIdNotFoundException;
import com.freeder.buclserver.global.exception.usershippingaddress.AddressUserNotMatchException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

	private final UserRepository userRepository;
	private final UserShippingAddressRepository userShippingAddressRepository;

	@Transactional
	public AddressCreateResponse createMyAddress(Long userId, AddressCreateRequest request) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		if (request.isDefaultAddress()) {
			userShippingAddressRepository.findByUserAndIsDefaultAddressIsTrue(user)
				.ifPresent(userAddress -> userAddress.cancelDefaultAddress());
		}

		UserShippingAddress userShippingAddress = UserShippingAddress.builder()
			.user(user)
			.shippingAddressName(request.shippingAddressName())
			.recipientName(request.recipientName())
			.zipCode(request.zipCode())
			.address(request.address())
			.addressDetail(request.addressDetail())
			.contactNumber(request.contactNumber())
			.isDefaultAddress(request.isDefaultAddress())
			.build();

		UserShippingAddress savedUserShippingAddress = userShippingAddressRepository.save(userShippingAddress);

		return AddressCreateResponse.from(savedUserShippingAddress);
	}

	@Transactional(readOnly = true)
	public List<UserShippingAddressDto> getMyAddressList(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		return userShippingAddressRepository.findAllByUser(user).stream()
			.map(UserShippingAddressDto::from)
			.collect(Collectors.toUnmodifiableList());
	}

	@Transactional
	public void deleteMyAddress(Long userId, Long addressId) {
		if (!userRepository.existsByIdAndDeletedAtIsNull(userId)) {
			throw new UserIdNotFoundException(userId);
		}

		UserShippingAddress userAddress = userShippingAddressRepository.findById(addressId)
			.orElseThrow(() -> new AddressIdNotFoundException(addressId));

		if (userAddress.getUser().getId() != userId) {
			throw new AddressUserNotMatchException();
		}

		userShippingAddressRepository.deleteById(addressId);
	}
}
