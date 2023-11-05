package com.freeder.buclserver.domain.orderexchange.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.orderexchange.vo.OrderExchangeExr;
import com.freeder.buclserver.domain.orderexchange.vo.OrderExchangeStatus;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "order_exchange")
public class OrderExchange extends TimestampMixin {
    @Id
    @Column(name = "order_exchange_code",unique = true,nullable = false)
    private String orderExchangeCode;

    @OneToOne
    @JoinColumn(name = "consumer_order_id")
    private ConsumerOrder consumerOrder;

    @OneToOne
    @JoinColumn(name = "order_exchange_shipping_id")
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
