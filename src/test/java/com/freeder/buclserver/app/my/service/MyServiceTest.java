package com.freeder.buclserver.app.my.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.freeder.buclserver.app.utils.UserTestUtils;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerorder.vo.CsStatus;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;
import com.freeder.buclserver.domain.consumerpayment.entity.ConsumerPayment;
import com.freeder.buclserver.domain.consumerpayment.repository.ConsumerPaymentRepository;
import com.freeder.buclserver.domain.consumerpayment.vo.PaymentMethod;
import com.freeder.buclserver.domain.consumerpayment.vo.PaymentStatus;
import com.freeder.buclserver.domain.consumerpayment.vo.PgProvider;
import com.freeder.buclserver.domain.consumerpurchaseorder.repository.ConsumerPurchaseOrderRepository;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippingaddress.repository.ShippingAddressRepository;
import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;
import com.freeder.buclserver.domain.user.dto.response.MyOrderDetailResponse;
import com.freeder.buclserver.domain.user.dto.response.MyOrderResponse;
import com.freeder.buclserver.domain.user.dto.response.MyProfileResponse;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.domain.user.util.ProfileImage;
import com.freeder.buclserver.global.exception.consumerorder.ConsumerUserNotMatchException;

@ExtendWith(MockitoExtension.class)
class MyServiceTest {

	@InjectMocks
	private MyService myService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RewardRepository rewardRepository;

	@Mock
	private ConsumerOrderRepository consumerOrderRepository;

	@Mock
	private ConsumerPurchaseOrderRepository consumerPurchaseOrderRepository;

	@Mock
	private ShippingRepository shippingRepository;

	@Mock
	private ShippingAddressRepository shippingAddressRepository;

	@Mock
	private ConsumerPaymentRepository consumerPaymentRepository;

	@Mock
	private ProfileS3Service profileS3Service;

	/**
	 * 자신의 프로필 조회 관련 테스트 코드
	 */
	@Test
	void 사용자의_PK를_받아_프로필을_조회한다() {
		// given
		Long userId = 1L;
		Integer rewardSum = 5000;
		User user = UserTestUtils.createUser();
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(rewardRepository.findUserRewardSum(userId, PageRequest.of(0, 1))).willReturn(List.of(rewardSum));

		// when
		MyProfileResponse myProfile = myService.getMyProfile(userId);

		// then
		assertThat(myProfile.profilePath()).isEqualTo(user.getProfilePath());
		assertThat(myProfile.nickname()).isEqualTo(user.getNickname());
		assertThat(myProfile.rewardSum()).isEqualTo(rewardSum);
	}

	@Test
	void 사용자_프로필_조회할_때_받은_리워드가_없다면_0으로_프로필을_반환한다() {
		// given
		Long userId = 1L;
		User user = UserTestUtils.createUser();
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(rewardRepository.findUserRewardSum(userId, PageRequest.of(0, 1))).willReturn(List.of());

		// when
		MyProfileResponse myProfile = myService.getMyProfile(userId);

		// then
		assertThat(myProfile.profilePath()).isEqualTo(user.getProfilePath());
		assertThat(myProfile.nickname()).isEqualTo(user.getNickname());
		assertThat(myProfile.rewardSum()).isEqualTo(0);
	}

	/**
	 * 디폴트 프로필 이미지로 변경 관련 테스트 코드
	 */
	@Test
	void 사용자의_기존_프로필_사진을_S3에서_삭제하고_디폴트_이미지로_변경한다() {
		// given
		User user = UserTestUtils.createUser();
		given(userRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(user));
		willDoNothing().given(profileS3Service).deleteFile(anyString());

		// when
		myService.updateProfileImageAsDefault(anyLong());

		// then
		assertThat(user.getProfilePath()).isEqualTo(ProfileImage.defaultImageUrl);
	}

	/**
	 * 원하는 프로필 이미지로 변경 관련 테스트 코드
	 */
	@Test
	void 사용자의_기존_프로필_사진을_S3에서_삭제하고_요청받은_사진을_S3에_저장하고_데이터를_수정한다() {
		// given
		User user = UserTestUtils.createUser();
		MockMultipartFile profileImage = new MockMultipartFile(
			"image", "image.jpg", MediaType.IMAGE_PNG_VALUE, "image-jpg".getBytes());
		String expectUploadFileUrl = "https://buclbucket.s3.ap-northeast-2.amazonaws.com/assets/images/profiles/image.png";
		given(userRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(user));
		willDoNothing().given(profileS3Service).deleteFile(anyString());
		given(profileS3Service.uploadFile(profileImage)).willReturn(expectUploadFileUrl);

		// when
		myService.updateProfileImage(anyLong(), profileImage);

		// then
		assertThat(user.getProfilePath()).isEqualTo(expectUploadFileUrl);
	}

	/**
	 * 자신의 모든 주문 내역 조회 관련 테스트 코드
	 */
	@Test
	void 사용자가_주문한_내역들을_리스트로_반환한다() {
		// given
		Long userId = 1L;
		User user = Mockito.spy(UserTestUtils.createUser());
		Pageable pageable = PageRequest.of(0, 10);
		ConsumerOrder consumerOrder1 = Mockito.spy(createConsumerOrder(user));
		ConsumerOrder consumerOrder2 = Mockito.spy(createConsumerOrder(user));
		Shipping shipping1 = createShipping(consumerOrder1, "12121", "23232", ShippingStatus.IN_DELIVERY, true);
		Shipping shipping2 = createShipping(consumerOrder2, "56565", "78787", ShippingStatus.IN_DELIVERY, true);
		doReturn(userId).when(user).getId();
		doReturn(1L).when(consumerOrder1).getId();
		doReturn(2L).when(consumerOrder2).getId();
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(consumerOrderRepository.findAllByConsumerOrderByCreatedAtDesc(user, pageable))
			.willReturn(List.of(consumerOrder1, consumerOrder2));
		given(consumerPurchaseOrderRepository.findProductOrderQty(consumerOrder1.getId())).willReturn(2);
		given(consumerPurchaseOrderRepository.findProductOrderQty(consumerOrder2.getId())).willReturn(5);
		given(shippingRepository.findByConsumerOrderAndIsActiveIsTrue(consumerOrder1))
			.willReturn(Optional.of(shipping1));
		given(shippingRepository.findByConsumerOrderAndIsActiveIsTrue(consumerOrder2))
			.willReturn(Optional.of(shipping2));

		// when
		List<MyOrderResponse> myOrders = myService.getMyOrders(userId, 1, 10);

		// then
		assertThat(myOrders.size()).isEqualTo(2);
		for (MyOrderResponse myOrder : myOrders) {
			assertThat(myOrder.shippingStatus()).isNotNull();
			assertThat(myOrder.deliveryTrackingUrl()).startsWith("https");
		}
	}

	/**
	 * 자신의 주문 상세 내역 조회 관련 테스트 코드
	 */
	@Test
	void 사용자가_주문한_내역의_상세_내역을_조회한다() {
		// given
		Long userId = 1L;
		Long consumerOrderId = 1L;
		int productOrderQty = 3;
		User consumer = Mockito.spy(UserTestUtils.createUser());
		ConsumerOrder consumerOrder = Mockito.spy(createConsumerOrder(consumer));
		ConsumerPayment consumerPayment = createConsumerPayment(consumerOrder);
		Shipping shipping = createShipping(consumerOrder, "12121", "23232", ShippingStatus.IN_DELIVERY, true);
		ShippingAddress shippingAddress = createShippingAddress(consumer, shipping);
		doReturn(userId).when(consumer).getId();
		doReturn(consumerOrderId).when(consumerOrder).getId();
		given(userRepository.existsByIdAndDeletedAtIsNull(userId)).willReturn(true);
		given(consumerOrderRepository.findById(consumerOrderId)).willReturn(Optional.of(consumerOrder));
		given(consumerPurchaseOrderRepository.findProductOrderQty(consumerOrderId)).willReturn(productOrderQty);
		given(consumerPaymentRepository.findByConsumerOrder(consumerOrder)).willReturn(consumerPayment);
		given(shippingRepository.findByConsumerOrderAndIsActiveIsTrue(consumerOrder)).willReturn(Optional.of(shipping));
		given(shippingAddressRepository.findByShipping(shipping)).willReturn(shippingAddress);

		// when
		MyOrderDetailResponse myOrderDetail = myService.getMyOrderDetail(userId, consumerOrderId);

		// then
		assertThat(myOrderDetail.rewardUseAmount()).isEqualTo(consumerOrder.getRewardUseAmount());
		assertThat(myOrderDetail.productOrderQty()).isEqualTo(productOrderQty);
		assertThat(myOrderDetail.addressDetail()).isEqualTo(shippingAddress.getAddressDetail());
		assertThat(myOrderDetail.paymentMethod()).isEqualTo(consumerPayment.getPaymentMethod());
	}

	@Test
	void 상품을_주문한_사용자와_조회하는_사용자가_다르면_에러가_발생한다() {
		// given
		Long userId = 1L;
		User wrongConsumer = Mockito.spy(UserTestUtils.createUser());
		ConsumerOrder consumerOrder = createConsumerOrder(wrongConsumer);
		doReturn(2L).when(wrongConsumer).getId();
		given(userRepository.existsByIdAndDeletedAtIsNull(userId)).willReturn(true);
		given(consumerOrderRepository.findById(anyLong())).willReturn(Optional.of(consumerOrder));

		// when
		Throwable throwable = catchThrowable(() -> myService.getMyOrderDetail(userId, anyLong()));

		// then
		assertThat(throwable).isInstanceOf(ConsumerUserNotMatchException.class);
	}

	private static ConsumerOrder createConsumerOrder(User consumer) {
		return ConsumerOrder.builder()
			.consumer(consumer)
			.product(new Product())
			.orderCode("orderCode")
			.orderNum(12345)
			.shippingFee(3000)
			.totalOrderAmount(50000)
			.rewardUseAmount(1000)
			.spentAmount(1000)
			.orderStatus(OrderStatus.ORDERED)
			.csStatus(CsStatus.NONE)
			.build();
	}

	private static ConsumerPayment createConsumerPayment(ConsumerOrder consumerOrder) {
		return ConsumerPayment.builder()
			.consumerOrder(consumerOrder)
			.pgTid("pdTid")
			.pgProvider(PgProvider.NAVERPAY)
			.paymentCode("paymentCode")
			.paymentAmount(15000)
			.consumerName("OOO")
			.consumerEmail("example@gmail.com")
			.consumerAddress("consumerAddress")
			.paymentStatus(PaymentStatus.READY)
			.paymentMethod(PaymentMethod.CARD)
			.paidAt(LocalDateTime.now())
			.build();
	}

	private static Shipping createShipping(
		ConsumerOrder consumerOrder, String shippingNum, String trackingNum,
		ShippingStatus shippingStatus, boolean isActive
	) {
		return Shipping.builder()
			.consumerOrder(consumerOrder)
			.shippingInfo(new ShippingInfo())
			.shippingNum(shippingNum)
			.trackingNum(trackingNum)
			.shippingStatus(shippingStatus)
			.isActive(isActive)
			.purchaseOrderInputDate(LocalDateTime.now())
			.trackingNumInputDate(LocalDateTime.now())
			.shippedDate(LocalDateTime.now())
			.build();
	}

	private static ShippingAddress createShippingAddress(User user, Shipping shipping) {
		return ShippingAddress.builder()
			.user(user)
			.shipping(shipping)
			.recipientName("recipientName")
			.zipCode("zipCode")
			.address("address")
			.addressDetail("addressDetail")
			.contactNumber("010-0000-0000")
			.memoContent("memoContent")
			.build();
	}
}