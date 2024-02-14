package com.freeder.buclserver.app.orderreturns;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.app.orderreturns.vo.OrdReturnReqDto;
import com.freeder.buclserver.app.orderreturns.vo.OrdReturnRespDto;
import com.freeder.buclserver.app.payment.PaymentService;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;
import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import com.freeder.buclserver.domain.grouporder.repository.GroupOrderRepository;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import com.freeder.buclserver.domain.orderrefund.repository.OrderRefundRepository;
import com.freeder.buclserver.domain.orderreturn.entity.OrderReturn;
import com.freeder.buclserver.domain.orderreturn.repository.OrderReturnRepository;
import com.freeder.buclserver.domain.orderreturn.vo.OrderReturnExr;
import com.freeder.buclserver.domain.orderreturn.vo.OrderReturnStatus;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.reward.service.RewardService;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.domain.user.vo.Role;
import com.freeder.buclserver.global.exception.servererror.BadRequestErrorException;
import com.freeder.buclserver.global.exception.servererror.InternalServerErrorException;
import com.freeder.buclserver.global.exception.servererror.UnauthorizedErrorException;
import com.freeder.buclserver.global.util.OrderReturnUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderReturnsService {
	private final OrderRefundRepository orderRefundRepository;
	private final OrderReturnRepository orderReturnRepository;
	private final UserRepository userRepository;
	private final ShippingRepository shippingRepository;
	private final ConsumerOrderRepository consumerOrderRepository;
	private final RewardRepository rewardRepository;
	private final GroupOrderRepository groupOrderRepository;

	private final PaymentService paymentService;
	private final RewardService rewardService;

	@Transactional
	public OrdReturnRespDto createOrderReturnApproval(String socialId, String orderCode,
		OrdReturnReqDto ordReturnReqDto) throws NullPointerException {
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
		Shipping prevShipping = shippingRepository.findFirstByConsumerOrderAndIsActive(consumerOrder, true).orElseThrow(
			() -> new InternalServerErrorException("배송 정보가 없습니다. 관리자에게 연락주세요.")
		);
		if (consumerOrder.isConfirmed()) {
			throw new BadRequestErrorException("상품 주문 확정이 되었습니다. 주문 반품이 불가능 합니다.");
		}
		if (!consumerOrder.getOrderStatus().equals(OrderStatus.ORDERED)) {
			throw new BadRequestErrorException(orderCode + "에 대해서 이미 CS 접수 된 상태입니다.");
		}
		if (prevShipping.getShippingStatus().equals(ShippingStatus.NOT_PROCESSING)) {
			throw new BadRequestErrorException("상품 주문 반품이 아니라 주문 취소를 하신 다음에 다시 주문 해주세요.");
		}

		int refundAmount = consumerOrder.getSpentAmount();
		int rewardUseAmount = consumerOrder.getRewardUseAmount();

		OrderRefund newOrderRefund = OrderRefund
			.builder()
			.refundAmount(refundAmount)
			.rewardUseAmount(rewardUseAmount)
			.build();

		newOrderRefund = orderRefundRepository.save(newOrderRefund);

		if (rewardUseAmount != 0) {
			User consumer = consumerOrder.getConsumer();
			rewardService.addRefundReward(consumer, consumerOrder, newOrderRefund, rewardUseAmount);
		}

		OrderReturn newOrderReturn = OrderReturn
			.builder()
			.orderReturnId(OrderReturnUtil.getReturnId())
			.orderReturnExr(OrderReturnExr.USER)
			.orderReturnStatus(OrderReturnStatus.COMPLETED)
			.completedAt(OrderReturnUtil.getCompletedAt())
			.orderRefund(newOrderRefund)
			.orderReturnFee(ordReturnReqDto.getReturnFee())
			.build();

		prevShipping.setShippingStatus(ShippingStatus.PICK_UP);
		consumerOrder.setOrderStatus(OrderStatus.ORDER_RETURNED);

		String impUid = consumerOrder.getOrderCode();

		consumerOrderRepository.save(consumerOrder);
		shippingRepository.save(prevShipping);
		newOrderReturn = orderReturnRepository.save(newOrderReturn);

		Optional<GroupOrder> optionalGroupOrder = groupOrderRepository.findByProductAndIsActiveAndCreatedBetween(
			consumerOrder.getProduct().getProductCode(), true, consumerOrder.getCreatedAt());
		if (optionalGroupOrder.isPresent()) {
			GroupOrder groupOrder = optionalGroupOrder.get();
			groupOrder.setActlNum(groupOrder.getActlNum() - 1);
		}
		paymentService.cancelPayment(impUid);

		return OrdReturnRespDto
			.builder()
			.orderReturnId(newOrderReturn.getOrderReturnId())
			.orderReturnStatus(OrderReturnStatus.COMPLETED)
			.build();
	}
}
