package com.freeder.buclserver.domain.reward.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.orderrefund.entity.OrderRefund;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.reward.vo.RewardType;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.entity.RewardWithdrawalAccount;
import com.freeder.buclserver.domain.user.entity.User;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "reward")
public class Reward extends TimestampMixin {
    @Id
    @Column(name = "reward_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = true)
    private Product product;

    @OneToOne
    @JoinColumn(name = "order_refund_id",nullable = true)
    private OrderRefund orderRefund;

    @ManyToOne
    @JoinColumn(name = "reward_withdrawal_account")
    private RewardWithdrawalAccount rewardWithdrawalAccount;

    @Column(name = "reward_type")
    @Enumerated(EnumType.STRING)
    private RewardType rewardType;

    private int received_reward_amount;

    private int reward_sum;

}
