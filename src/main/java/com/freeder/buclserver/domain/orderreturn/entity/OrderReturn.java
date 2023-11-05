package com.freeder.buclserver.domain.orderreturn.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import com.freeder.buclserver.domain.orderreturn.vo.OrderReturnExr;
import com.freeder.buclserver.domain.orderreturn.vo.OrderReturnStatus;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "order_return")
public class OrderReturn extends TimestampMixin {
    @Id
    @Column(name = "order_return_code",unique = true,nullable = false)
    private String orderReturnCode;

    @ManyToOne
    @JoinColumn(name = "consumer_order_id")
    private ConsumerOrder consumerOrder;

    @OneToOne
    @JoinColumn(name = "order_refund_code")
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
