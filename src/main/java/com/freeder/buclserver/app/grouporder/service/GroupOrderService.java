package com.freeder.buclserver.app.grouporder.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import com.freeder.buclserver.domain.grouporder.repository.GroupOrderRepository;
import com.freeder.buclserver.global.util.GroupOrderUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional()
public class GroupOrderService {
	private final GroupOrderRepository groupOrderRepository;
	private final ConsumerOrderRepository consumerOrderRepository;

	@Scheduled(cron = "0 08 16 * * *")
	public void startGroupOrder() {
		log.info(
			"{\"status\":\"scheduling\", \"msg\":\"New Group Order Created And Start\", \"createdAt:"
				+ "\"" + GroupOrderUtil.getStartGroupOrderDateTime() + "\""
				+ "}"
		);

		List<GroupOrder> groupOrders = groupOrderRepository.findByIsActive(true);
		for (GroupOrder groupOrder : groupOrders) {
			if (groupOrder.getProduct().isExposed()) {
				continue;
			}
			try {
				LocalDateTime startedAt = groupOrder.getStartedAt();
				LocalDateTime endedAt = groupOrder.getEndedAt();
				if (startedAt.isBefore(LocalDateTime.now()) && endedAt.isAfter(LocalDateTime.now())
					&& groupOrder.isActive()) {
					if (groupOrder.isNeededUpdate()) {
						Integer groupOrderNum = consumerOrderRepository.countByProductCodeWithGroupOrderCondition(
							groupOrder.getProduct().getProductCode(), groupOrder.getStartedAt(),
							groupOrder.getEndedAt());
						groupOrder.setActlNum(groupOrderNum);
					}
				}
			} catch (NullPointerException exception) {
				log.info(
					"{\"status\":\"scheduling-error\", \"msg\":\"시작일 하고 종료일 다시 한번 더 확인 해 주세요.\","
						+ "\"group_order_object\":\"" + groupOrder.getId() + "\""
						+ "}"
				);
			}
		}
	}

	@Scheduled(cron = "0 19 16 * * *")
	public void updateGroupOrderEnded() {
		log.info(
			"{\"status\":\"scheduling\", \"msg\":\"Group Order Updated And end\", updatedAt:"
				+ "\"" + GroupOrderUtil.getEndGroupOrderDateTime() + "\""
				+ "}"
		);

		List<GroupOrder> groupOrders = groupOrderRepository.findByIsActive(true);
		for (GroupOrder groupOrder : groupOrders) {
			if (!groupOrder.getProduct().isExposed()) {
				continue;
			}
			try {
				LocalDateTime endedAt = groupOrder.getStartedAt();
				if (endedAt.isBefore(LocalDateTime.now())) {
					System.out.println("ㅎ하하하");
					groupOrder.setActive(false);
					groupOrder.setDeadline(false);
					groupOrder.setStartedAt(null);
					groupOrder.setEndedAt(null);
					groupOrder.setCtrlNum(0);
					groupOrder.setActlNum(0);
				}
			} catch (NullPointerException exception) {
				log.info(
					"{\"status\":\"scheduling-error\", \"msg\":\"시작일 하고 종료일 다시 한번 더 확인 해 주세요.\","
						+ "\"group_order_object\":\"" + groupOrder.getId() + "\""
						+ "}"
				);
			}
		}
	}

}
