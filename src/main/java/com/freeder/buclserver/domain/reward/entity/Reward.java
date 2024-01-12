package com.freeder.buclserver.domain.reward.entity;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.reward.vo.RewardType;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.entity.RewardWithdrawalAccount;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reward")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reward extends TimestampMixin {

	@Id
	@Column(name = "reward_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@ManyToOne
	@JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Product product;

	@OneToOne
	@JoinColumn(name = "consumer_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ConsumerOrder consumerOrder;

	@OneToOne
	@JoinColumn(name = "order_refund_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private OrderRefund orderRefund;

	@ManyToOne
	@JoinColumn(name = "reward_withdrawal_account", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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

	@Builder
	private Reward(
		User user, Product product, ConsumerOrder consumerOrder, OrderRefund orderRefund,
		RewardWithdrawalAccount rewardWithdrawalAccount, String productName, String productBrandName,
		RewardType rewardType, int receivedRewardAmount, int spentRewardAmount, int previousRewardSum, int rewardSum
	) {
		this.user = user;
		this.product = product;
		this.consumerOrder = consumerOrder;
		this.orderRefund = orderRefund;
		this.rewardWithdrawalAccount = rewardWithdrawalAccount;
		this.productName = productName;
		this.productBrandName = productBrandName;
		this.rewardType = rewardType;
		this.receivedRewardAmount = receivedRewardAmount;
		this.spentRewardAmount = spentRewardAmount;
		this.previousRewardSum = previousRewardSum;
		this.rewardSum = rewardSum;
	}
}
