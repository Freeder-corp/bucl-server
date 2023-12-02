package com.freeder.buclserver.domain.reward.service;

import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.reward.vo.RewardType;
import com.freeder.buclserver.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardService {
	private final RewardRepository rewardRepository;

	public Reward addRefundReward(User consumer, ConsumerOrder consumerOrder, OrderRefund orderRefund,
		int rewardUseAmount) {
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
		return rewardRepository.save(reward);
	}
}
