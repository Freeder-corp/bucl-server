package com.freeder.buclserver.app.orders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.app.orders.dto.OrderDetailDto;
import com.freeder.buclserver.app.orders.dto.OrderDto;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.member.entity.Member;
import com.freeder.buclserver.domain.member.repository.MemberRepository;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippingaddress.repository.ShippingAddressRepository;
import com.freeder.buclserver.domain.shippinginfo.repository.ShippingInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {

	private final ConsumerOrderRepository consumerOrderRepository;
	private final MemberRepository memberRepository;
	private final ShippingRepository shippingRepository;
	private final ShippingAddressRepository shippingAddressRepository;
	private final ShippingInfoRepository shippingInfoRepository;

	public OrderDetailDto readOrderDetail(String socialId, String orderCode) {
		Member member = memberRepository.findBySocialId(socialId).orElseThrow();
		ConsumerOrder consumerOrder = consumerOrderRepository.findByOrderCode(orderCode).orElseThrow();
		Shipping shipping = shippingRepository.findFirstByConsumerOrderAndIsActive(consumerOrder, true).orElseThrow();
		ShippingAddress shippingAddress = shippingAddressRepository.findByShipping(shipping).orElseThrow();
		return OrderDetailDto.from(consumerOrder, shipping, shippingAddress);
	}

	public List<OrderDto> readOrderList(String socialId, Pageable pageable) {
		Member member = memberRepository.findBySocialId(socialId).orElseThrow();
		List<ConsumerOrder> consumerOrderList = consumerOrderRepository.findAllByConsumerOrderByCreatedAtDesc(member,
			pageable).stream().toList();

		List<OrderDto> orderDtoList = new ArrayList<>();
		for (ConsumerOrder consumerOrder : consumerOrderList) {
			orderDtoList.add(OrderDto.from(consumerOrder));
		}

		return orderDtoList;
	}
}
