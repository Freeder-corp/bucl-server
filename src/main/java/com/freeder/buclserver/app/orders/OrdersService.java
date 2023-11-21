package com.freeder.buclserver.app.orders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.app.orders.dto.OrderDetailDto;
import com.freeder.buclserver.app.orders.dto.OrderDto;
import com.freeder.buclserver.app.orders.dto.ShpAddrDto;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.member.entity.Member;
import com.freeder.buclserver.domain.member.repository.MemberRepository;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippingaddress.repository.ShippingAddressRepository;
import com.freeder.buclserver.domain.shippinginfo.repository.ShippingInfoRepository;
import com.freeder.buclserver.global.exception.BaseException;

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

	public ShpAddrDto updateOrderShpAddr(String orderCode, ShpAddrDto shpAddrDto) {
		ConsumerOrder consumerOrder = consumerOrderRepository.findByOrderCode(orderCode).orElseThrow();
		Shipping shipping = shippingRepository.findFirstByConsumerOrderAndIsActive(consumerOrder, true).orElseThrow();
		ShippingStatus shpStatus = shipping.getShippingStatus();
		if (shpStatus != ShippingStatus.NOT_PROCESSING) {
			throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
				"상품이 준비 되어 배송을 수정할 수 없습니다.");
		}
		ShippingAddress shippingAddress = shippingAddressRepository.findByShipping(shipping).orElseThrow();

		shippingAddress.updateEntity(
			shpAddrDto.getRecipientNam(),
			shpAddrDto.getZipCode(),
			shpAddrDto.getAddress(),
			shpAddrDto.getAddressDetail(),
			shpAddrDto.getContactNumber()
		);

		shippingAddressRepository.save(shippingAddress);

		return shpAddrDto;
	}
}
