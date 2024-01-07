package com.freeder.buclserver.app.orders;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.app.orders.dto.ConsumerOrderDto;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;
import com.freeder.buclserver.domain.consumerpurchaseorder.entity.ConsumerPurchaseOrder;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.domain.user.vo.Role;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {
	private final ConsumerOrderRepository consumerOrderRepository;
	private final UserRepository userRepository;

	@Transactional
	public BaseResponse<List<ConsumerOrderDto>> getOrdersDocument(Long productId, Long userId) {
		if (!userRepository.existsByIdAndRole(userId, Role.ROLE_ADMIN)) {
			throw new BaseException(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다.");
		}

		List<ConsumerOrder> consumerOrders = consumerOrderRepository.findByProduct_IdAndOrderStatusOrderByCreatedAtDesc(
			productId, OrderStatus.ORDERED);

		if (consumerOrders.isEmpty()) {
			throw new BaseException(HttpStatus.BAD_REQUEST, 400, "구매자가없습니다.");
		}

		// List<ConsumerOrder> consumerOrders = orders.stream()
		// 	.filter(consumerOrder ->
		// 		consumerOrder.getOrderStatus().equals(OrderStatus.ORDERED)
		// 	)
		// 	.toList();

		String productName = consumerOrders.get(0).getProduct().getName();

		List<ConsumerOrderDto> orderDtos = consumerOrders.stream()
			.map(consumerOrder -> convertConsumerOrders(productName, consumerOrder))
			.toList();

		return new BaseResponse<>(orderDtos, HttpStatus.OK, "요청 성공");
	}

	// @Transactional
	// public BaseResponse<String> updateOrderPurchase(List<PurchaseOrderDto> purchaseOrderDtos, Long userId) {
	// 	if (!userRepository.existsByIdAndRole(userId, Role.ROLE_ADMIN)) {
	// 		throw new BaseException(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다.");
	// 	}
	// 	for (PurchaseOrderDto purchaseOrderDto : purchaseOrderDtos) {
	// 		ConsumerOrder consumerOrder = consumerOrderRepository.findByOrderCode(purchaseOrderDto.getOrderCode())
	// 			.orElseThrow(() ->
	// 				new BaseException(HttpStatus.BAD_REQUEST, 400, "잘못된필드")
	// 			);
	//
	// 		// Shipping shipping = consumerOrder.getShippings().stream()
	// 		// 	.filter(Shipping::isActive)
	// 		// 	.findFirst()
	// 		// 	.orElseThrow(() ->
	// 		// 		new BaseException(HttpStatus.BAD_REQUEST, 400, "활성화된 배송정보가 없습니다.")
	// 		// 	);
	// 		//
	// 		// shipping.setShippingStatus(ShippingStatus.PROCESSING);
	//
	// 		consumerOrder.setOrderStatus(OrderStatus.ORDERED_PROCESSING);
	// 	}
	// 	return new BaseResponse<>("해당 주문 리스트 발주 넣었습니다.", HttpStatus.OK, "요청 성공");
	// }
	//
	// @Transactional
	// public BaseResponse<String> updateTrackingNum(List<TrackingNumDto> trackingNumDtos, Long userId) {
	// 	if (!userRepository.existsByIdAndRole(userId, Role.ROLE_ADMIN)) {
	// 		throw new BaseException(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다.");
	// 	}
	//
	// 	for (TrackingNumDto trackingNumDto : trackingNumDtos) {
	// 		ConsumerOrder consumerOrder = consumerOrderRepository.findByOrderCode(
	// 				trackingNumDto.getOrderCode())
	// 			.orElseThrow(() ->
	// 				new BaseException(HttpStatus.BAD_REQUEST, 400,
	// 					"주문번호: " + trackingNumDto.getOrderCode() + " 는 존재하지 않습니다.")
	// 			);
	// 		if (consumerOrder.getOrderStatus() != OrderStatus.ORDERED_PROCESSING) {
	// 			throw new BaseException(HttpStatus.BAD_REQUEST, 400,
	// 				"주문번호: " + trackingNumDto.getOrderCode() + " 는 발주 상태가 아닙니다.");
	// 		}
	//
	// 		Shipping shipping = consumerOrder.getShippings().stream()
	// 			.filter(Shipping::isActive)
	// 			.findFirst()
	// 			.orElseThrow(() ->
	// 				new BaseException(HttpStatus.BAD_REQUEST, 400, "활성화된 배송정보가 없습니다.")
	// 			);
	//
	// 		consumerOrder.setOrderStatus(OrderStatus.ORDERED_DELIVERED);
	// 		// shipping.setShippingStatus(ShippingStatus.IN_DELIVERY);
	// 		System.out.println(trackingNumDto.getTrackingNum());
	// 		shipping.setTrackingNum(trackingNumDto.getTrackingNum());
	// 		shipping.setShippingCoName(trackingNumDto.getShippingCoName());
	// 	}
	// 	return new BaseResponse<>("해당 주문 리스트에 대한 운송장 번호를 입력했습니다.", HttpStatus.OK, "요청 성공");
	// }

	//private 영역//

	private ConsumerOrderDto convertConsumerOrders(String name, ConsumerOrder consumerOrder) {
		try {
			List<ConsumerOrderDto.ProductOption> list = consumerOrder.getConsumerPurchaseOrders().stream()
				.map(this::convertConsumerPurchaseOrder)
				.toList();

			Shipping shipping = consumerOrder.getShippings().stream()
				.filter(Shipping::isActive)
				.findFirst()
				.orElseThrow(() ->
					new BaseException(HttpStatus.BAD_REQUEST, 400, "활성화된 배송이 없습니다.")
				);

			// shipping.setShippingStatus(ShippingStatus.PROCESSING);

			ShippingAddress shippingAddress = shipping.getShippingAddress();
			ShippingInfo shippingInfo = shipping.getShippingInfo();

			return ConsumerOrderDto.builder()
				.orderCode(consumerOrder.getOrderCode())
				.name(name)
				.productOptions(list)
				.recipientName(shippingAddress.getRecipientName())
				.zipCode(shippingAddress.getZipCode())
				.address(shippingAddress.getAddress())
				.addressDetail(shippingAddress.getAddressDetail())
				.contactNumber(shippingAddress.getContactNumber())
				.memoContent(shippingAddress.getMemoContent())
				.shippingFee(shippingInfo.getShippingFee())
				.shippingFeePhrase(shippingInfo.getShippingFeePhrase())
				.build();
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "convertConsumerOrders오류 개발자에게 문의바랍니다.");
		}
	}

	private ConsumerOrderDto.ProductOption convertConsumerPurchaseOrder(ConsumerPurchaseOrder consumerPurchaseOrder) {
		return ConsumerOrderDto.ProductOption.builder()
			.productOptionValue(consumerPurchaseOrder.getProductOptionValue())
			.productOptionQty(consumerPurchaseOrder.getProductOrderQty())
			.build();
	}
}