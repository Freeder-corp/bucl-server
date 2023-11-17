package com.freeder.buclserver.domain.ordercancel.entity;

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
import com.freeder.buclserver.domain.ordercancel.vo.OrderCancelExr;
import com.freeder.buclserver.domain.ordercancel.vo.OrderCancelStatus;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "order_cancel")
public class OrderCancel extends TimestampMixin {
	@Id
	@Column(name = "order_cancel_code", unique = true, nullable = false)
	private String orderCancelCode;

	@OneToOne
	@JoinColumn(name = "consumer_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ConsumerOrder consumerOrder;

	@OneToOne
	@JoinColumn(name = "order_refund_code", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private OrderRefund orderRefund;

	@Column(name = "order_cancel_status")
	@Enumerated(EnumType.STRING)
	private OrderCancelStatus orderCancelStatus;

	@Column(name = "order_cancel_exr")
	@Enumerated(EnumType.STRING)
	private OrderCancelExr orderCancelExr;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;
}
