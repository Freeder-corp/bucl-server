package com.freeder.buclserver.domain.consumerpurchaseorder.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.productoption.entity.ProductOption;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "consumer_purchase_order")
public class ConsumerPurchaseOrder extends TimestampMixin {
    @Id
    @Column(name = "consumer_purchase_order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "consumer_order_id")
    private ConsumerOrder consumerOrder;

    @ManyToOne
    @JoinColumn(name = "group_order_id")
    private ProductOption productOption;

    @Column(name = "product_order_code",unique = true)
    private String productOrderCode;

    @Column(name = "product_amount")
    private int productAmount;

    @Column(name = "product_order_num")
    private int productOrderNum;

    @Column(name = "product_order_amount")
    private int productOrderAmount;
}
