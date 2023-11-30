package com.freeder.buclserver.app.orders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerpurchaseorder.repository.ConsumerPurchaseOrderRepository;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.user.dto.response.MyOrderResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrdersService {

	private final ConsumerOrderRepository consumerOrderRepository;
	private final ConsumerPurchaseOrderRepository consumerPurchaseOrderRepository;
	private final ShippingRepository shippingRepository;

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
}
