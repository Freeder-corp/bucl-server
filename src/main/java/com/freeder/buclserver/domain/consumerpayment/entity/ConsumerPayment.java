package com.freeder.buclserver.domain.consumerpayment.entity;

import java.time.LocalDateTime;

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
import javax.persistence.Table;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerpayment.vo.PaymentMethod;
import com.freeder.buclserver.domain.consumerpayment.vo.PaymentStatus;
import com.freeder.buclserver.domain.consumerpayment.vo.PgProvider;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "consumer_payment")
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ConsumerPayment extends TimestampMixin {
	@Id
	@Column(name = "consumer_payment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "consumer_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ConsumerOrder consumerOrder;

	@Column(name = "pg_tid")
	private String pgTid;

	@Enumerated(EnumType.STRING)
	@Column(name = "pg_provider")
	private PgProvider pgProvider;

	@Column(name = "payment_code")
	private String paymentCode;

	@Column(name = "payment_amount")
	private int paymentAmount;

	@Column(name = "consumer_name")
	private String consumerName;

	@Column(name = "consumer_email", length = 320)
	private String consumerEmail;

	@Column(name = "consumer_address")
	private String consumerAddress;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status")
	private PaymentStatus paymentStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method")
	private PaymentMethod paymentMethod;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;
}