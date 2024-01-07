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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CONSUMER_PAYMENT")
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

	@Builder
	private ConsumerPayment(
		ConsumerOrder consumerOrder, String pgTid, PgProvider pgProvider, String paymentCode, int paymentAmount,
		String consumerName, String consumerEmail, String consumerAddress, PaymentStatus paymentStatus,
		PaymentMethod paymentMethod, LocalDateTime paidAt
	) {
		this.consumerOrder = consumerOrder;
		this.pgTid = pgTid;
		this.pgProvider = pgProvider;
		this.paymentCode = paymentCode;
		this.paymentAmount = paymentAmount;
		this.consumerName = consumerName;
		this.consumerEmail = consumerEmail;
		this.consumerAddress = consumerAddress;
		this.paymentStatus = paymentStatus;
		this.paymentMethod = paymentMethod;
		this.paidAt = paidAt;
	}
}
