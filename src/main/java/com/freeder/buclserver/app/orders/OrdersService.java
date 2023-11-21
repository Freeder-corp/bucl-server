package com.freeder.buclserver.app.orders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.app.orders.dto.OrderDto;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.member.entity.Member;
import com.freeder.buclserver.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {
	private String testSocialId = "sjfdlkwjlkj149202";

	private final ConsumerOrderRepository consumerOrderRepository;
	private final MemberRepository memberRepository;

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
