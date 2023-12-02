package com.freeder.buclserver.app.ordercancels;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.app.ordercancels.dto.OrderCancelResponseDto;
import com.freeder.buclserver.app.payment.PaymentService;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerorder.vo.CsStatus;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;
import com.freeder.buclserver.domain.ordercancel.entity.OrderCancel;
import com.freeder.buclserver.domain.ordercancel.repository.OrderCancelRepository;
import com.freeder.buclserver.domain.ordercancel.vo.OrderCancelExr;
import com.freeder.buclserver.domain.ordercancel.vo.OrderCancelStatus;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import com.freeder.buclserver.domain.orderrefund.repository.OrderRefundRepository;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.reward.vo.RewardType;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.domain.user.vo.Role;
import com.freeder.buclserver.global.exception.servererror.BadRequestErrorException;
import com.freeder.buclserver.global.exception.servererror.InternalServerErrorException;
import com.freeder.buclserver.global.exception.servererror.UnauthorizedErrorException;
import com.freeder.buclserver.global.util.OrderCancelUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderCancelsService {
	private final OrderRefundRepository orderRefundRepository;
	private final OrderCancelRepository orderCancelRepository;
	private final UserRepository userRepository;
	private final ShippingRepository shippingRepository;
	private final ConsumerOrderRepository consumerOrderRepository;
	private final RewardRepository rewardRepository;

	private final PaymentService paymentService;

	@Transactional
	public OrderCancelResponseDto createOrderCancel(String socialId, String orderCode) throws NullPointerException {
		User consumer = userRepository.findBySocialId(socialId).orElseThrow(
			() -> new UnauthorizedErrorException("인증 실패 했습니다.")
		);
		ConsumerOrder consumerOrder = consumerOrderRepository.findByOrderCodeAndConsumer(orderCode, consumer)
			.orElseThrow(
				() -> new BadRequestErrorException("해당 주문코드에 대한 주문내역이 없습니다.")
			);
		Shipping shipping = shippingRepository.findFirstByConsumerOrderAndIsActive(consumerOrder, true).orElseThrow(
			() -> new InternalServerErrorException("배송 정보가 없습니다. 관리자에게 연락주세요.")
		);
		if (consumerOrder.isConfirmed()) {
			throw new BadRequestErrorException("상품 주문 확정이 되었습니다. 주문 취소를 원하시면 고객센터로 전화 주세요.");
		}
		if (!shipping.getShippingStatus().equals(ShippingStatus.NOT_PROCESSING)) {
			throw new BadRequestErrorException("상품이 이미 준비가 되어서 주문 취소를 할 수 없습니다. 반품 및 교환 신청해주세요.");
		}
		if (consumerOrder.getCsStatus().equals(CsStatus.ORDER_CANCEL)) {
			throw new BadRequestErrorException("이미 주문 취소가 되었습니다.");
		}

		consumerOrder.setCsStatus(CsStatus.ORDER_CANCEL);
		consumerOrder.setOrderStatus(OrderStatus.ORDER_CANCELING);
		consumerOrderRepository.save(consumerOrder);

		int refundAmount = consumerOrder.getSpentAmount();
		int rewardUseAmount = consumerOrder.getRewardUseAmount();

		OrderRefund orderRefund = OrderRefund
			.builder()
			.refundAmount(refundAmount)
			.rewardUseAmount(rewardUseAmount)
			.build();

		OrderRefund newOrderRefund = orderRefundRepository.save(orderRefund);
		OrderCancel orderCancel = OrderCancel
			.builder()
			.user(consumer)
			.orderCancelId(OrderCancelUtil.getOrderId())
			.orderCancelExr(OrderCancelExr.USER)
			.orderCancelStatus(OrderCancelStatus.RECEIVED)
			.orderRefund(newOrderRefund)
			.consumerOrder(consumerOrder)
			.build();

		OrderCancel newOrderCancel = orderCancelRepository.save(orderCancel);
		return OrderCancelResponseDto
			.builder()
			.orderCancelId(newOrderCancel.getOrderCancelId())
			.refundAmount(newOrderRefund.getRefundAmount())
			.rewardUseAmount(newOrderRefund.getRewardUseAmount())
			.build();
	}

	@Transactional
	public void updateOrderCancelApproval(String socialId, String orderCode) {
		User admin = userRepository.findBySocialId(socialId).orElseThrow(
			() -> new UnauthorizedErrorException("인증 실패 했습니다.")
		);
		if (!admin.getRole().equals(Role.ROLE_ADMIN)) {
			throw new UnauthorizedErrorException("해당 기능은 관리자만 쓸 수 있습니다.");
		}

		ConsumerOrder consumerOrder = consumerOrderRepository.findByOrderCode(orderCode)
			.orElseThrow(
				() -> new BadRequestErrorException("해당 주문 건은 없습니다.")
			);

		if (!consumerOrder.getCsStatus().equals(CsStatus.ORDER_CANCEL)) {
			throw new BadRequestErrorException("주문 취소 요청 상태가 아닙니다.");
		}
		if (consumerOrder.getOrderStatus().equals(OrderStatus.ORDER_CANCELED)) {
			throw new BadRequestErrorException("이미 주문 취소가 완료되었습니다.");
		}

		OrderCancel orderCancel = orderCancelRepository.findByConsumerOrder(consumerOrder).orElseThrow(
			() -> new InternalServerErrorException("해당 주문 코드에 대한 주문 취소 정보가 없습니다.")
		);

		OrderRefund orderRefund = orderCancel.getOrderRefund();

		int rewardUseAmount = orderRefund.getRewardUseAmount();
		User consumer = consumerOrder.getConsumer();

		if (rewardUseAmount != 0) {
			int previousRewardAmt = rewardRepository.findFirstByUserId(consumer.getId()).orElse(0);

			Product product = consumerOrder.getProduct();
			Reward reward = Reward
				.builder()
				.user(consumer)
				.rewardType(RewardType.REFUND)
				.previousRewardSum(previousRewardAmt)
				.consumerOrder(consumerOrder)
				.receivedRewardAmount(rewardUseAmount)
				.product(product)
				.productName(product.getName())
				.productBrandName(product.getBrandName())
				.rewardSum(previousRewardAmt + rewardUseAmount)
				.orderRefund(orderRefund)
				.build();
			rewardRepository.save(reward);
		}

		consumerOrder.setOrderStatus(OrderStatus.ORDER_CANCELED);
		consumerOrder.setCsStatus(CsStatus.NONE);
		orderCancel.setOrderCancelStatus(OrderCancelStatus.COMPLETED);
		orderCancel.setCompletedAt(OrderCancelUtil.getCompletedAt());

		String impUid = consumerOrder.getOrderCode();

		consumerOrderRepository.save(consumerOrder);
		orderCancelRepository.save(orderCancel);
		orderRefundRepository.save(orderRefund);

		paymentService.cancelPayment(impUid);
	}
}
