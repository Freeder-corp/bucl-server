package com.freeder.buclserver.domain.reward.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.reward.vo.RewardType;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.entity.RewardWithdrawalAccount;
import com.freeder.buclserver.domain.user.entity.User;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "reward")
public class Reward extends TimestampMixin {
	@Id
	@Column(name = "reward_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = true)
	private Product product;

	@OneToOne
	@JoinColumn(name = "consumer_order_id")
	private ConsumerOrder consumerOrder;

	@OneToOne
	@JoinColumn(name = "order_refund_id", nullable = true)
	private OrderRefund orderRefund;

	@ManyToOne
	@JoinColumn(name = "reward_withdrawal_account")
	private RewardWithdrawalAccount rewardWithdrawalAccount;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "product_brand_name")
	private String productBrandName;

	@Column(name = "reward_type")
	@Enumerated(EnumType.STRING)
	private RewardType rewardType;

	@Column(name = "received_reward_amount")
	private int receivedRewardAmount;

	@Column(name = "spent_reward_amount")
	private int spentRewardAmount;

	@Column(name = "previous_reward_sum")
	private int previousRewardSum;

	@Column(name = "reward_sum")
	private int rewardSum;

}
