package com.freeder.buclserver.app.my.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.affiliate.entity.Affiliate;
import com.freeder.buclserver.domain.affiliate.repository.AffiliateRepository;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerpurchaseorder.repository.ConsumerPurchaseOrderRepository;
import com.freeder.buclserver.domain.payment.entity.Payment;
import com.freeder.buclserver.domain.payment.repository.PaymentRepository;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippingaddress.repository.ShippingAddressRepository;
import com.freeder.buclserver.domain.user.dto.UserDto;
import com.freeder.buclserver.domain.user.dto.response.MyAffiliateResponse;
import com.freeder.buclserver.domain.user.dto.response.MyOrderDetailResponse;
import com.freeder.buclserver.domain.user.dto.response.MyOrderResponse;
import com.freeder.buclserver.domain.user.dto.response.MyProfileResponse;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.global.exception.auth.WithdrawalBadRequestException;
import com.freeder.buclserver.global.exception.consumerorder.ConsumerOrderIdNotFoundException;
import com.freeder.buclserver.global.exception.consumerorder.ConsumerUserNotMatchException;
import com.freeder.buclserver.global.exception.user.UserIdNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyService {

	private final UserRepository userRepository;
	private final RewardRepository rewardRepository;
	private final AffiliateRepository affiliateRepository;
	private final ConsumerOrderRepository consumerOrderRepository;
	private final ConsumerPurchaseOrderRepository consumerPurchaseOrderRepository;
	private final ShippingRepository shippingRepository;
	private final ShippingAddressRepository shippingAddressRepository;
	private final PaymentRepository paymentRepository;

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

	@Transactional(readOnly = true)
	public List<MyAffiliateResponse> getMyAffiliates(Long userId) {
		List<MyAffiliateResponse> affiliateResponseList = new ArrayList<>();

		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		List<Affiliate> affiliateList = affiliateRepository.findAllByUserOrderByCreatedAtDesc(user);

		for (Affiliate affiliate : affiliateList) {
			int totalReceivedReward = rewardRepository.findReceivedRewardAmount(
				affiliate.getUser().getId(),
				affiliate.getProduct().getId()
			);
			MyAffiliateResponse affiliateResponse = MyAffiliateResponse.from(affiliate, totalReceivedReward);
			affiliateResponseList.add(affiliateResponse);
		}

		return affiliateResponseList;
	}

	@Transactional(readOnly = true)
	public List<MyOrderResponse> getMyOrders(Long userId) {
		List<MyOrderResponse> orderResponseList = new ArrayList<>();

		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		List<ConsumerOrder> consumerOrderList =
			consumerOrderRepository.findAllByConsumerOrderByCreatedAtDesc(user);

		for (ConsumerOrder consumerOrder : consumerOrderList) {
			// TODO: 1개의 주문에 대해 한 옵션만 선택 가능함에 따라 로직 변경 필요
			int totalProductQty = consumerPurchaseOrderRepository.findTotalProductOrderQty(consumerOrder.getId());
			Shipping shipping = shippingRepository.findByConsumerOrder(consumerOrder);
			MyOrderResponse orderResponse = MyOrderResponse.from(consumerOrder, totalProductQty, shipping);
			orderResponseList.add(orderResponse);
		}

		return orderResponseList;
	}

	@Transactional(readOnly = true)
	public MyOrderDetailResponse getMyOrderDetail(Long userId, Long consumerOrderId) {
		if (!userRepository.existsByIdAndDeletedAtIsNull(userId)) {
			throw new UserIdNotFoundException(userId);
		}

		ConsumerOrder consumerOrder = consumerOrderRepository.findById(consumerOrderId)
			.orElseThrow(() -> new ConsumerOrderIdNotFoundException(consumerOrderId));

		if (consumerOrder.getConsumer().getId() != userId) {
			throw new ConsumerUserNotMatchException();
		}

		// TODO: 1개의 주문에 대해 한 옵션만 선택 가능함에 따라 로직 변경 필요
		int totalProductQty = consumerPurchaseOrderRepository.findTotalProductOrderQty(consumerOrder.getId());
		Shipping shipping = shippingRepository.findByConsumerOrder(consumerOrder);
		ShippingAddress shippingAddress = shippingAddressRepository.findByShipping(shipping);
		Payment payment = paymentRepository.findByConsumerOrder(consumerOrder);

		return MyOrderDetailResponse.from(consumerOrder, shippingAddress, payment, totalProductQty);
	}

	private MyProfileResponse getMyProfileWithoutReward(String profilePath, String nickname) {
		return MyProfileResponse.of(profilePath, nickname, 0);
	}
}
