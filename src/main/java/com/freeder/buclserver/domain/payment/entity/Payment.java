package com.freeder.buclserver.domain.payment.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.payment.vo.PGProvider;
import com.freeder.buclserver.domain.payment.vo.PaymentStatus;
import com.freeder.buclserver.domain.payment.vo.PaymentMethod;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "PAYMENT")
public class Payment extends TimestampMixin {
    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "consumer_order_id")
    private ConsumerOrder consumerOrder;

    @Column(name = "pg_tid")
    private String pgTid;

    @Enumerated(EnumType.STRING)
    @Column(name = "pg_provider")
    private PGProvider pgProvider;

    @Column(name = "payment_code")
    private String paymentCode;

    @Column(name = "payment_amount")
    private int paymentAmount;

    @Column(name = "consumer_name")
    private String consumerName;

    @Column(name = "consumer_email",length = 320)
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
