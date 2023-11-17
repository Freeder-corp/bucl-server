package com.freeder.buclserver.domain.orderexchange.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.orderexchange.vo.OrderExchangeExr;
import com.freeder.buclserver.domain.orderexchange.vo.OrderExchangeStatus;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "order_exchange")
public class OrderExchange extends TimestampMixin {
	@Id
	@Column(name = "order_exchange_code", unique = true, nullable = false)
	private String orderExchangeCode;

	@OneToOne
	@JoinColumn(name = "consumer_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ConsumerOrder consumerOrder;

	@OneToOne
	@JoinColumn(name = "order_exchange_shipping_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Shipping orderExchangeShipping;

	@Column(name = "orderExchange_fee")
	private int orderExchangeFee;

	@Column(name = "order_exchange_exr")
	@Enumerated(EnumType.STRING)
	private OrderExchangeExr orderExchangeExr;

	@Column(name = "order_exchange_status")
	@Enumerated(EnumType.STRING)
	private OrderExchangeStatus orderExchangeStatus;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;
}
