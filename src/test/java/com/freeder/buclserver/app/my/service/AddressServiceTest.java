package com.freeder.buclserver.app.my.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.domain.usershippingaddress.dto.UserShippingAddressDto;
import com.freeder.buclserver.domain.usershippingaddress.dto.request.AddressCreateRequest;
import com.freeder.buclserver.domain.usershippingaddress.dto.request.AddressUpdateRequest;
import com.freeder.buclserver.domain.usershippingaddress.entity.UserShippingAddress;
import com.freeder.buclserver.domain.usershippingaddress.exception.AddressUserNotMatchException;
import com.freeder.buclserver.domain.usershippingaddress.exception.AlreadyDefaultAddressException;
import com.freeder.buclserver.domain.usershippingaddress.exception.SingleAddressDefaultRegisterException;
import com.freeder.buclserver.domain.usershippingaddress.repository.UserShippingAddressRepository;
import com.freeder.buclserver.util.UserTestUtil;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

	@InjectMocks
	private AddressService addressService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserShippingAddressRepository userShippingAddressRepository;

	/**
	 * 배송지 생성 관련 테스트 코드
	 */
	@Test
	void 배송지_등록이_처음이라면_요청_데이터의_디폴트_주소_값과_상관없이_디폴트_배송지로_저장한다() {
		// given
		User user = UserTestUtil.create();
		AddressCreateRequest request = createAddressCreateRequest(false);
		UserShippingAddress expectSavedAddress = createShippingAddress(true);
		given(userRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(user));
		given(userShippingAddressRepository.existsByUser(any(User.class))).willReturn(false);
		given(userShippingAddressRepository.save(any(UserShippingAddress.class))).willReturn(expectSavedAddress);

		// when
		UserShippingAddressDto actualSavedAddress = addressService.createMyAddress(anyLong(), request);

		// then
		then(userShippingAddressRepository).should().save(any(UserShippingAddress.class));
		assertThat(actualSavedAddress.isDefaultAddress()).isTrue();
	}

	@Test
	void 요청_데이터의_디폴트_주소_값이_true라면_기존의_디폴트_주소를_기본_배송지로_변경하고_새로운_주소를_저장한다() {
		// given
		User user = UserTestUtil.create();
		AddressCreateRequest request = createAddressCreateRequest(true);
		UserShippingAddress expectSavedAddress = createShippingAddress(true);
		given(userRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(user));
		given(userShippingAddressRepository.existsByUser(any(User.class))).willReturn(true);
		given(userShippingAddressRepository.save(any(UserShippingAddress.class))).willReturn(expectSavedAddress);

		// when
		UserShippingAddressDto actualSavedAddress = addressService.createMyAddress(anyLong(), request);

		// then
		then(userShippingAddressRepository).should().findByUserAndIsDefaultAddressIsTrue(anyLong());
		then(userShippingAddressRepository).should().save(any(UserShippingAddress.class));
		assertThat(actualSavedAddress.isDefaultAddress()).isTrue();
	}

	@Test
	void 배송지_등록이_처음이_아니면서_요청_데이터의_디폴트_주소_값이_false라면_바로_새로운_주소로_저장한다() {
		// given
		User user = UserTestUtil.create();
		AddressCreateRequest request = createAddressCreateRequest(false);
		UserShippingAddress expectSavedAddress = createShippingAddress(false);
		given(userRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(user));
		given(userShippingAddressRepository.existsByUser(any(User.class))).willReturn(true);
		given(userShippingAddressRepository.save(any(UserShippingAddress.class))).willReturn(expectSavedAddress);

		// when
		UserShippingAddressDto actualSavedAddress = addressService.createMyAddress(anyLong(), request);

		// then
		then(userShippingAddressRepository).should().save(any(UserShippingAddress.class));
		assertThat(actualSavedAddress.isDefaultAddress()).isEqualTo(request.isDefaultAddress());
	}

	/**
	 * 모든 배송지 조회 관련 테스트 코드
	 */
	@Test
	void 사용자의_모든_주소_리스트_조회한다() {
		// given
		User user = UserTestUtil.create();
		List<UserShippingAddress> expectAddressList = List.of(
			createShippingAddress(true),
			createShippingAddress(false));
		given(userRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(user));
		given(userShippingAddressRepository.findAllByUser(user)).willReturn(expectAddressList);

		// when
		List<UserShippingAddressDto> actualAddressList = addressService.getMyAddressList(anyLong());

		// then
		then(userShippingAddressRepository).should().findAllByUser(any(User.class));
		assertThat(actualAddressList.size()).isEqualTo(expectAddressList.size());
	}

	/**
	 * 배송지 수정 관련 테스트 코드
	 */
	@Test
	void 디폴트_주소_취소와_함께_배송지를_수정한다면_사용자의_가장_최근에_등록한_배송지를_디폴트_주소로_등록하고_데이터를_수정한다() {
		// given
		Long userId = 1L;
		Long addressId = 1L;
		User user = Mockito.spy(UserTestUtil.create());
		UserShippingAddress existAddress = Mockito.spy(createShippingAddressWithUser(user, true));
		AddressUpdateRequest expectUpdateAddress = createAddressUpdateRequest(false);
		doReturn(userId).when(user).getId();
		doReturn(addressId).when(existAddress).getId();
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(userShippingAddressRepository.findById(addressId)).willReturn(Optional.of(existAddress));
		given(userShippingAddressRepository.countByUser(user)).willReturn(5L);

		// when
		UserShippingAddressDto actualUpdateAddress = addressService.updateMyAddress(userId, addressId,
			expectUpdateAddress);

		// then
		then(userShippingAddressRepository).should().findByUserAndIsDefaultAddressIsTrue(anyLong());
		then(userShippingAddressRepository).should().findFirstByUserOrderByIdDesc(any(User.class));
		assertThat(actualUpdateAddress)
			.hasFieldOrPropertyWithValue("shippingAddressName", expectUpdateAddress.shippingAddressName())
			.hasFieldOrPropertyWithValue("recipientName", expectUpdateAddress.recipientName())
			.hasFieldOrPropertyWithValue("contactNumber", expectUpdateAddress.contactNumber())
			.hasFieldOrPropertyWithValue("zipCode", expectUpdateAddress.zipCode())
			.hasFieldOrPropertyWithValue("address", expectUpdateAddress.address())
			.hasFieldOrPropertyWithValue("addressDetail", expectUpdateAddress.addressDetail())
			.hasFieldOrPropertyWithValue("isDefaultAddress", expectUpdateAddress.isDefaultAddress());
	}

	@Test
	void 디폴트_주소로의_변경과_함께_배송지를_수정한다면_기존_디폴트_배송지를_제거하고_나머지_배송지_데이터를_수정한다() {
		// given
		Long userId = 1L;
		User user = Mockito.spy(UserTestUtil.create());
		UserShippingAddress existAddress = createShippingAddressWithUser(user, false);
		AddressUpdateRequest expectUpdateAddress = createAddressUpdateRequest(true);
		doReturn(userId).when(user).getId();
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(userShippingAddressRepository.findById(anyLong())).willReturn(Optional.of(existAddress));
		given(userShippingAddressRepository.countByUser(user)).willReturn(3L);

		// when
		UserShippingAddressDto actualUpdateAddress = addressService.updateMyAddress(userId, anyLong(),
			expectUpdateAddress);

		// then
		then(userShippingAddressRepository).should().findByUserAndIsDefaultAddressIsTrue(anyLong());
		assertThat(actualUpdateAddress)
			.hasFieldOrPropertyWithValue("shippingAddressName", expectUpdateAddress.shippingAddressName())
			.hasFieldOrPropertyWithValue("recipientName", expectUpdateAddress.recipientName())
			.hasFieldOrPropertyWithValue("contactNumber", expectUpdateAddress.contactNumber())
			.hasFieldOrPropertyWithValue("zipCode", expectUpdateAddress.zipCode())
			.hasFieldOrPropertyWithValue("address", expectUpdateAddress.address())
			.hasFieldOrPropertyWithValue("addressDetail", expectUpdateAddress.addressDetail())
			.hasFieldOrPropertyWithValue("isDefaultAddress", true);
	}

	@Test
	void 배송지를_수정하려는_사용자와_등록한_사용자가_다르다면_에러_발생한다() {
		// given
		Long userId = 1L;
		User user = UserTestUtil.create();
		User wrongUser = Mockito.spy(UserTestUtil.create());
		UserShippingAddress existAddress = createShippingAddressWithUser(wrongUser, false);
		AddressUpdateRequest expectAddress = createAddressUpdateRequest(true);
		doReturn(2L).when(wrongUser).getId();
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(userShippingAddressRepository.findById(anyLong())).willReturn(Optional.of(existAddress));

		// when
		Throwable throwable = catchThrowable(() -> addressService.updateMyAddress(userId, anyLong(), expectAddress));

		// then
		assertThat(throwable).isInstanceOf(AddressUserNotMatchException.class);
	}

	@Test
	void 등록된_배송지가_1개일_때_디폴트_주소를_false로_변경하려고_하면_에러_발생한다() {
		// given
		Long userId = 1L;
		User user = Mockito.spy(UserTestUtil.create());
		UserShippingAddress existAddress = createShippingAddressWithUser(user, false);
		AddressUpdateRequest expectAddress = createAddressUpdateRequest(false);
		doReturn(userId).when(user).getId();
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(userShippingAddressRepository.findById(anyLong())).willReturn(Optional.of(existAddress));
		given(userShippingAddressRepository.countByUser(user)).willReturn(1L);

		// when
		Throwable throwable = catchThrowable(() -> addressService.updateMyAddress(userId, anyLong(), expectAddress));

		// then
		assertThat(throwable).isInstanceOf(SingleAddressDefaultRegisterException.class);
	}

	/**
	 * 배송지 삭제 관련 테스트 코드
	 */
	@Test
	void 배송지_PK를_받아_DB에서_배송지를_삭제한다() {
		// given
		Long userId = 1L;
		User user = Mockito.spy(UserTestUtil.create());
		UserShippingAddress deleteAddress = createShippingAddressWithUser(user, false);
		doReturn(userId).when(user).getId();
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(userShippingAddressRepository.findById(anyLong())).willReturn(Optional.of(deleteAddress));
		willDoNothing().given(userShippingAddressRepository).deleteById(anyLong());

		// when
		addressService.deleteMyAddress(userId, anyLong());

		// then
		then(userShippingAddressRepository).should().deleteById(anyLong());
	}

	@Test
	void 삭제_요청을_받은_배송지가_디폴트_배송지였다면_사용자의_최근_배송지를_디폴트_배송지로_수정하고_삭제한다() {
		// given
		Long userId = 1L;
		User user = Mockito.spy(UserTestUtil.create());
		UserShippingAddress deleteAddress = createShippingAddressWithUser(user, false);
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(userShippingAddressRepository.findById(anyLong())).willReturn(Optional.of(deleteAddress));
		given(user.getId()).willReturn(userId);
		willDoNothing().given(userShippingAddressRepository).deleteById(anyLong());

		// when
		addressService.deleteMyAddress(userId, anyLong());

		// then
		then(userShippingAddressRepository).should().deleteById(anyLong());
		// then(userShippingAddressRepository).should().findFirstByUserOrderByIdDesc(any(User.class));
	}

	@Test
	void 배송지를_삭제하려는_사용자와_등록한_사용자가_다르다면_에러_발생한다() {
		// given
		Long userId = 1L;
		User user = UserTestUtil.create();
		User wrongUser = Mockito.spy(UserTestUtil.create());
		UserShippingAddress userShippingAddress = createShippingAddressWithUser(wrongUser, false);
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(userShippingAddressRepository.findById(anyLong())).willReturn(Optional.of(userShippingAddress));
		doReturn(2L).when(wrongUser).getId();

		// when
		Throwable throwable = catchThrowable(() -> addressService.deleteMyAddress(userId, anyLong()));

		// then
		assertThat(throwable).isInstanceOf(AddressUserNotMatchException.class);
	}

	/**
	 * 디폴트 배송지 조회 관련 테스트 코드
	 */
	@Test
	void 사용자의_디폴트_배송지_조회한다() {
		// given
		UserShippingAddress expectDefaultAddress = createShippingAddress(true);
		given(userRepository.existsByIdAndDeletedAtIsNull(anyLong())).willReturn(true);
		given(userShippingAddressRepository.findByUserAndIsDefaultAddressIsTrue(anyLong()))
			.willReturn(Optional.of(expectDefaultAddress));

		// when
		UserShippingAddressDto actualDefaultAddress = addressService.getMyDefaultAddress(anyLong());

		// then
		then(userShippingAddressRepository).should().findByUserAndIsDefaultAddressIsTrue(anyLong());
		assertThat(actualDefaultAddress.isDefaultAddress()).isTrue();
	}

	/**
	 * 디폴트 배송지 수정 관련 테스트 코드
	 */
	@Test
	void 사용자가_원하는_주소를_디폴트_주소로_변경한다() {
		// given
		Long userId = 1L;
		User user = Mockito.spy(UserTestUtil.create());
		UserShippingAddress existAddress = createShippingAddressWithUser(user, false);
		given(userRepository.existsByIdAndDeletedAtIsNull(userId)).willReturn(true);
		given(userShippingAddressRepository.findById(anyLong())).willReturn(Optional.of(existAddress));
		doReturn(userId).when(user).getId();

		// when
		UserShippingAddressDto updateAddress = addressService.updateMyDefaultAddress(userId, anyLong());

		// then
		then(userShippingAddressRepository).should().findByUserAndIsDefaultAddressIsTrue(anyLong());
		assertThat(updateAddress.isDefaultAddress()).isTrue();
	}

	@Test
	void 이미_디폴트_주소인것을_디폴트_주소로_변경할_경우_에러_발생한다() {
		// given
		Long userId = 1L;
		UserShippingAddress existAddress = createShippingAddress(true);
		given(userRepository.existsByIdAndDeletedAtIsNull(userId)).willReturn(true);
		given(userShippingAddressRepository.findById(anyLong())).willReturn(Optional.of(existAddress));

		// when
		Throwable throwable = catchThrowable(() -> addressService.updateMyDefaultAddress(userId, anyLong()));

		// then
		assertThat(throwable).isInstanceOf(AlreadyDefaultAddressException.class);
	}

	@Test
	void 디폴트_배송지로_수정하려는_사용자와_등록한_사용자가_다르다면_에러_발생한다() {
		// given
		Long userId = 1L;
		User wrongUser = Mockito.spy(UserTestUtil.create());
		UserShippingAddress address = createShippingAddressWithUser(wrongUser, false);
		given(userRepository.existsByIdAndDeletedAtIsNull(userId)).willReturn(true);
		given(userShippingAddressRepository.findById(anyLong())).willReturn(Optional.of(address));
		doReturn(2L).when(wrongUser).getId();

		// when
		Throwable throwable = catchThrowable(() -> addressService.updateMyDefaultAddress(userId, anyLong()));

		// then
		assertThat(throwable).isInstanceOf(AddressUserNotMatchException.class);
	}

	private static UserShippingAddress createShippingAddressWithUser(User user, boolean isDefaultAddress) {
		return UserShippingAddress.builder()
			.user(user)
			.addrNo("addrNo")
			.shippingAddressName("배송지 이름")
			.recipientName("수취인 이름")
			.zipCode("12345")
			.address("xx도 xx시 xx동")
			.addressDetail("xx동 xx호")
			.contactNumber("010-1234-5678")
			.isDefaultAddress(isDefaultAddress)
			.build();
	}

	private static UserShippingAddress createShippingAddress(boolean isDefaultAddress) {
		return createShippingAddressWithUser(UserTestUtil.create(), isDefaultAddress);
	}

	private static AddressCreateRequest createAddressCreateRequest(boolean isDefaultAddress) {
		return AddressCreateRequest.builder()
			.shippingAddressName("배송지 이름")
			.recipientName("수취인 이름")
			.zipCode("12345")
			.address("xx도 xx시 xx동")
			.addressDetail("111동 111호")
			.contactNumber("010-1234-5678")
			.isDefaultAddress(isDefaultAddress)
			.build();
	}

	private static AddressUpdateRequest createAddressUpdateRequest(boolean isDefaultAddress) {
		return AddressUpdateRequest.builder()
			.shippingAddressName("변경할 배송지 이름")
			.recipientName("변경할 수취인 이름")
			.zipCode("67890")
			.address("xx도 xx시 xx동")
			.addressDetail("222동 2222호")
			.contactNumber("010-9876-4321")
			.isDefaultAddress(isDefaultAddress)
			.build();
	}
}