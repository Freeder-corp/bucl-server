package com.freeder.buclserver.app.my.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.affiliate.repository.AffiliateRepository;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerpayment.entity.ConsumerPayment;
import com.freeder.buclserver.domain.consumerpayment.repository.ConsumerPaymentRepository;
import com.freeder.buclserver.domain.consumerpurchaseorder.repository.ConsumerPurchaseOrderRepository;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippingaddress.repository.ShippingAddressRepository;
import com.freeder.buclserver.domain.user.dto.UserDto;
import com.freeder.buclserver.domain.user.dto.response.MyOrderDetailResponse;
import com.freeder.buclserver.domain.user.dto.response.MyOrderResponse;
import com.freeder.buclserver.domain.user.dto.response.MyProfileResponse;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.global.exception.auth.WithdrawalBadRequestException;
import com.freeder.buclserver.global.exception.consumerorder.ConsumerUserNotMatchException;
import com.freeder.buclserver.global.exception.consumerorder.OrderIdNotFoundException;
import com.freeder.buclserver.global.exception.user.UserIdNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MyService {

	private final UserRepository userRepository;
	private final RewardRepository rewardRepository;
	private final AffiliateRepository affiliateRepository;
	private final ConsumerOrderRepository consumerOrderRepository;
	private final ConsumerPurchaseOrderRepository consumerPurchaseOrderRepository;
	private final ShippingRepository shippingRepository;
	private final ShippingAddressRepository shippingAddressRepository;
	private final ConsumerPaymentRepository consumerPaymentRepository;
	private final ProfileS3Service profileS3Service;

	@Transactional(readOnly = true)
	public Optional<UserDto> findBySocialIdAndDeletedAtIsNull(String socialUid) {
		return userRepository.findBySocialIdAndDeletedAtIsNull(socialUid)
			.map(UserDto::from);
	}

	@Transactional
	public UserDto join(UserDto userDto) {
		User user = userRepository.save(userDto.toEntity());
		return UserDto.from(user);
	}

	@Transactional
	public void deleteRefreshToken(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		user.deleteRefreshToken();
	}

	@Transactional
	public void withdrawal(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		if (user.getRefreshToken() == null) {
			throw new WithdrawalBadRequestException();
		}

		user.withdrawal();
	}

	@Transactional(readOnly = true)
	public MyProfileResponse getMyProfile(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		return rewardRepository.findFirstByUserOrderByCreatedAtDesc(user)
			.map(MyProfileResponse::from)
			.orElseGet(() -> getMyProfileWithoutReward(user.getProfilePath(), user.getNickname()));
	}

	@Transactional
	public void updateProfileImageAsDefault(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		profileS3Service.deleteFile(user.getProfilePath());

		user.updateProfilePathAsDefault();
	}

	@Transactional(readOnly = true)
	public List<MyOrderResponse> getMyOrders(Long userId, int page, int pageSize) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		Pageable pageable = PageRequest.of(page - 1, pageSize);

		return consumerOrderRepository.findAllByConsumerOrderByCreatedAtDesc(user, pageable).getContent().stream()
			.filter(consumerOrder -> consumerOrder.getConsumer().getId() == userId)
			.map(this::getMyOrderInfo)
			.collect(Collectors.toUnmodifiableList());
	}

	@Transactional(readOnly = true)
	public MyOrderDetailResponse getMyOrderDetail(Long userId, Long consumerOrderId) {
		if (!userRepository.existsByIdAndDeletedAtIsNull(userId)) {
			throw new UserIdNotFoundException(userId);
		}

		ConsumerOrder consumerOrder = consumerOrderRepository.findById(consumerOrderId)
			.orElseThrow(() -> new OrderIdNotFoundException(consumerOrderId));

		if (consumerOrder.getConsumer().getId() != userId) {
			throw new ConsumerUserNotMatchException();
		}

		int productOrderQty = consumerPurchaseOrderRepository.findProductOrderQty(consumerOrder.getId());
		ConsumerPayment payment = consumerPaymentRepository.findByConsumerOrder(consumerOrder);

		return shippingRepository.findByConsumerOrderAndIsActiveIsTrue(consumerOrder)
			.map(shipping -> {
				ShippingAddress shippingAddress = shippingAddressRepository.findByShipping(shipping);
				return MyOrderDetailResponse.from(consumerOrder, shippingAddress, payment, productOrderQty);
			})
			.orElseGet(() -> MyOrderDetailResponse.from(consumerOrder, null, payment, productOrderQty));
	}

	private MyProfileResponse getMyProfileWithoutReward(String profilePath, String nickname) {
		return MyProfileResponse.of(profilePath, nickname, 0);
	}

	private MyOrderResponse getMyOrderInfo(ConsumerOrder consumerOrder) {
		int productOrderQty = consumerPurchaseOrderRepository.findProductOrderQty(consumerOrder.getId());

		return shippingRepository.findByConsumerOrderAndIsActiveIsTrue(consumerOrder)
			.map(shipping -> MyOrderResponse.from(consumerOrder, productOrderQty, shipping))
			.orElseGet(() -> MyOrderResponse.from(consumerOrder, productOrderQty, null));
	}
}
