package com.freeder.buclserver.app.orders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freeder.buclserver.app.orders.dto.OrderDetailDto;
import com.freeder.buclserver.app.orders.dto.OrderDto;
import com.freeder.buclserver.app.orders.dto.ShpAddrDto;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippingaddress.repository.ShippingAddressRepository;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.exception.servererror.BadRequestErrorException;
import com.freeder.buclserver.global.exception.servererror.InternalServerErrorException;
import com.freeder.buclserver.global.exception.servererror.UnauthorizedErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {

	private final RestTemplate restTemplate;

	private final ConsumerOrderRepository consumerOrderRepository;
	private final UserRepository userRepository;
	private final ShippingRepository shippingRepository;
	private final ShippingAddressRepository shippingAddressRepository;

	@Value("${tracking_info.t_key}")
	private String trackingTKey;

	public OrderDetailDto readOrderDetail(String socialId, String orderCode) {
		ConsumerOrder consumerOrder = consumerOrderRepository.findByOrderCode(orderCode)
			.orElseThrow(
				() -> new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "주문 정보가 없습니다."));
		if (!consumerOrder.getConsumer().getSocialId().equals(socialId)) {
			throw new UnauthorizedErrorException("해당 주문 정보를 볼 권한이 없습니다.");
		}
		Shipping shipping = shippingRepository.findFirstByConsumerOrderAndIsActive(consumerOrder, true)
			.orElseThrow(() -> new InternalServerErrorException("주문에 대한 배송 정보가 없습니다. 서버상에 문제가 발생했습니다."));
		ShippingAddress shippingAddress = shippingAddressRepository.findByShipping(shipping)
			.orElseThrow(() -> new InternalServerErrorException("주문에 대한 배송지 정보가 없습니다. 서버상에 문제가 발생했습니다."));
		return OrderDetailDto.from(consumerOrder, shipping, shippingAddress);
	}

	public List<OrderDto> readOrderList(String socialId, Pageable pageable) {
		User user = userRepository.findBySocialId(socialId)
			.orElseThrow(() -> new BadRequestErrorException("해당 유저가 없습니다."));
		List<ConsumerOrder> consumerOrderList = consumerOrderRepository.findAllByConsumerOrderByCreatedAtDesc(user,
			pageable).stream().toList();

		List<OrderDto> orderDtoList = new ArrayList<>();
		for (ConsumerOrder consumerOrder : consumerOrderList) {
			orderDtoList.add(OrderDto.from(consumerOrder));
		}
		return orderDtoList;
	}

	public ShpAddrDto updateOrderShpAddr(String orderCode, ShpAddrDto shpAddrDto) {
		ConsumerOrder consumerOrder = consumerOrderRepository.findByOrderCode(orderCode)
			.orElseThrow(
				() -> new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "주문 정보가 없습니다."));
		Shipping shipping = shippingRepository.findFirstByConsumerOrderAndIsActive(consumerOrder, true)
			.orElseThrow(() -> new InternalServerErrorException("주문에 대한 배송 정보가 없습니다. 서버상에 문제가 발생했습니다."));
		ShippingStatus shpStatus = shipping.getShippingStatus();
		if (shpStatus != ShippingStatus.NOT_PROCESSING) {
			throw new BadRequestErrorException("상품이 준비 되어 배송을 수정할 수 없습니다.");
		}
		ShippingAddress shippingAddress = shippingAddressRepository.findByShipping(shipping)
			.orElseThrow(() -> new InternalServerErrorException("배송 정보가 없습니다. 서버상에 문제가 발생했습니다."));

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
