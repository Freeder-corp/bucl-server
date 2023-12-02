package com.freeder.buclserver.domain.orderrefund.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "order_refund")
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class OrderRefund extends TimestampMixin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_refund_id", nullable = false)
	private Long id;

	@Column(name = "refund_amount", nullable = false)
	private int refundAmount;

	@Column(name = "reward_use_amount")
	private int rewardUseAmount;

}
