package com.freeder.buclserver.domain.orderreturn.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import com.freeder.buclserver.domain.orderreturn.vo.OrderReturnExr;
import com.freeder.buclserver.domain.orderreturn.vo.OrderReturnStatus;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "order_return")
public class OrderReturn extends TimestampMixin {
	@Id
	@Column(name = "order_return_id", unique = true, nullable = false)
	private String orderReturnId;

	@ManyToOne
	@JoinColumn(name = "consumer_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ConsumerOrder consumerOrder;

	@OneToOne
	@JoinColumn(name = "order_refund_code", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private OrderRefund orderRefund;

	@Column(name = "order_return_fee")
	private int orderReturnFee;

	@Column(name = "order_return_exr")
	@Enumerated(EnumType.STRING)
	private OrderReturnExr orderReturnExr;

	@Column(name = "order_return_status")
	@Enumerated(EnumType.STRING)
	private OrderReturnStatus orderReturnStatus;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;
}
