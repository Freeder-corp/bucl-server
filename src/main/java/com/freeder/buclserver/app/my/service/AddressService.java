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
import com.freeder.buclserver.global.exception.usershippingaddress.DefaultAddressNotFoundException;

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

		if (!userShippingAddressRepository.existsByUser(user)) {
			UserShippingAddress userShippingAddress = createUserAddressEntity(user, request, true);
			UserShippingAddress savedUserShippingAddress = userShippingAddressRepository.save(userShippingAddress);
			return AddressCreateResponse.from(savedUserShippingAddress);
		}

		if (request.isDefaultAddress()) {
			userShippingAddressRepository.findByUserAndIsDefaultAddressIsTrue(userId)
				.ifPresent(userAddress -> userAddress.cancelDefaultAddress());
		}

		UserShippingAddress userShippingAddress = createUserAddressEntity(user, request, request.isDefaultAddress());
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
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		UserShippingAddress deleteUserAddress = userShippingAddressRepository.findById(addressId)
			.orElseThrow(() -> new AddressIdNotFoundException(addressId));

		userShippingAddressRepository.deleteById(addressId);

		if (deleteUserAddress.isDefaultAddress() == true) {
			userShippingAddressRepository.findFirstByUserOrderByIdDesc(user)
				.ifPresent(address -> address.registerDefaultAddress());
		}
	}

	@Transactional(readOnly = true)
	public UserShippingAddressDto getMyDefaultAddress(Long userId) {
		if (!userRepository.existsByIdAndDeletedAtIsNull(userId)) {
			throw new UserIdNotFoundException(userId);
		}

		UserShippingAddress userAddress = userShippingAddressRepository.findByUserAndIsDefaultAddressIsTrue(userId)
			.orElseThrow(DefaultAddressNotFoundException::new);

		return UserShippingAddressDto.from(userAddress);
	}

	@Transactional
	public UserShippingAddressDto updateMyDefaultAddress(Long userId, Long addressId) {
		if (!userRepository.existsByIdAndDeletedAtIsNull(userId)) {
			throw new UserIdNotFoundException(userId);
		}

		UserShippingAddress registerAddress = userShippingAddressRepository.findById(addressId)
			.orElseThrow(() -> new AddressIdNotFoundException(addressId));

		userShippingAddressRepository.findByUserAndIsDefaultAddressIsTrue(userId)
			.ifPresent(address -> address.cancelDefaultAddress());

		registerAddress.registerDefaultAddress();

		return UserShippingAddressDto.from(registerAddress);
	}

	private UserShippingAddress createUserAddressEntity(
		User user, AddressCreateRequest request, boolean isDefaultAddress
	) {
		return UserShippingAddress.builder()
			.user(user)
			.shippingAddressName(request.shippingAddressName())
			.recipientName(request.recipientName())
			.zipCode(request.zipCode())
			.address(request.address())
			.addressDetail(request.addressDetail())
			.contactNumber(request.contactNumber())
			.isDefaultAddress(isDefaultAddress)
			.build();
	}
}