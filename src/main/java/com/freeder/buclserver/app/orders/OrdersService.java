package com.freeder.buclserver.app.orders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerpurchaseorder.repository.ConsumerPurchaseOrderRepository;
import com.freeder.buclserver.domain.payment.entity.Payment;
import com.freeder.buclserver.domain.payment.repository.PaymentRepository;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippingaddress.repository.ShippingAddressRepository;
import com.freeder.buclserver.domain.user.dto.response.MyOrderDetailResponse;
import com.freeder.buclserver.domain.user.dto.response.MyOrderResponse;
import com.freeder.buclserver.global.exception.consumerorder.ConsumerOrderIdNotFoundException;
import com.freeder.buclserver.global.exception.consumerorder.ConsumerUserNotMatchException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrdersService {

	private final ConsumerOrderRepository consumerOrderRepository;
	private final ConsumerPurchaseOrderRepository consumerPurchaseOrderRepository;
	private final ShippingRepository shippingRepository;
	private final ShippingAddressRepository shippingAddressRepository;
	private final PaymentRepository paymentRepository;

	@Transactional(readOnly = true)
	public List<MyOrderResponse> getMyOrders(Long userId) {
		List<MyOrderResponse> orderResponseList = new ArrayList<>();

		List<ConsumerOrder> consumerOrderList =
			consumerOrderRepository.findAllByConsumer_IdOrderByCreatedAtDesc(userId);

		for (ConsumerOrder consumerOrder : consumerOrderList) {
			// TODO: 1개의 주문에 대해 한 옵션만 선택 가능함에 따라 로직 변경 필요
			int totalProductQty = consumerPurchaseOrderRepository.findTotalProductOrderQty(consumerOrder.getId());
			Shipping shipping = shippingRepository.findByConsumerOrder_Id(consumerOrder.getId());
			MyOrderResponse orderResponse = MyOrderResponse.from(consumerOrder, totalProductQty, shipping);
			orderResponseList.add(orderResponse);
		}

		return orderResponseList;
	}

	@Transactional(readOnly = true)
	public MyOrderDetailResponse getMyOrderDetail(Long userId, Long consumerOrderId) {
		ConsumerOrder consumerOrder = consumerOrderRepository.findById(consumerOrderId)
			.orElseThrow(() -> new ConsumerOrderIdNotFoundException(consumerOrderId));

		if (consumerOrder.getConsumer().getId() != userId) {
			throw new ConsumerUserNotMatchException();
		}

		// TODO: 1개의 주문에 대해 한 옵션만 선택 가능함에 따라 로직 변경 필요
		int totalProductQty = consumerPurchaseOrderRepository.findTotalProductOrderQty(consumerOrder.getId());
		Shipping shipping = shippingRepository.findByConsumerOrder_Id(consumerOrderId);
		ShippingAddress shippingAddress = shippingAddressRepository.findByShipping_Id(shipping.getId());
		Payment payment = paymentRepository.findByConsumerOrder_Id(consumerOrderId);

		return MyOrderDetailResponse.from(consumerOrder, shippingAddress, payment, totalProductQty);
	}
}
