package com.freeder.buclserver.domain.ordercancel.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.ordercancel.vo.OrderCancelExr;
import com.freeder.buclserver.domain.ordercancel.vo.OrderCancelStatus;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "order_cancel")
public class OrderCancel extends TimestampMixin {
    @Id
    @Column(name = "order_cancel_code",unique = true,nullable = false)
    private String orderCancelCode;

    @OneToOne
    @JoinColumn(name = "consumer_order_id")
    private ConsumerOrder consumerOrder;

    @OneToOne
    @JoinColumn(name = "order_refund_code")
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
