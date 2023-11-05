package com.freeder.buclserver.domain.consumerorder.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.consumerpurchaseorder.entity.ConsumerPurchaseOrder;
import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import com.freeder.buclserver.domain.consumerorder.vo.CSStatus;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;
import com.freeder.buclserver.domain.payment.entity.Payment;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.user.entity.User;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "consumer_order")
public class ConsumerOrder extends TimestampMixin {
    @Id
    @Column(name = "consumer_order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "consumer_id", nullable = false)
    private User consumer;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private User business;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "reward_id")
    private Reward reward;

    @ManyToOne
    @JoinColumn(name = "group_order_id")
    private GroupOrder groupOrder;

    @OneToMany(mappedBy = "consumerOrder")
    private List<Shipping> shippings = new ArrayList<>();

    @OneToMany(mappedBy = "consumerOrder")
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "consumerOrder")
    private List<ConsumerPurchaseOrder> consumerPurchaseOrders = new ArrayList<>();

    @Column(name = "order_code",unique = true)
    private String orderCode;

    @Column(name = "product_amount")
    private int productAmount;

    @Column(name = "product_order_num")
    private int productOrderNum;

    @Column(name = "shipping_fee")
    private int shippingFee;

    @Column(name = "total_order_amount")
    private int totalOrderAmount;

    @Column(name = "reward_use_amount")
    private int rewardUseAmount;

    @Column(name = "spent_amount")
    private int spentAmount;

    @ColumnDefault("false")
    @Column(name = "is_rewarded",nullable = false)
    private boolean is_rewarded;

    @ColumnDefault("false")
    @Column(name = "is_confirmed")
    private boolean isConfirmed;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "cs_status")
    private CSStatus csStatus;
}
