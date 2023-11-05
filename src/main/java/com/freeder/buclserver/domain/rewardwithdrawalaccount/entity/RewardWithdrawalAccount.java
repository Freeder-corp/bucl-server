package com.freeder.buclserver.domain.rewardwithdrawalaccount.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.user.entity.User;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "reward_withdrawal_account")
public class RewardWithdrawalAccount extends TimestampMixin {
    @Id
    @Column(name = "reward_withdrawal_account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "rewardWithdrawalAccount")
    private List<Reward> rewards = new ArrayList<>();

    @ColumnDefault("false")
    @Column(name = "is_authenticated")
    private boolean isAuthenticated;

    @Column(name = "bank_code_std")
    private String bankCodeStd;

    @Column(name = "bank_name")
    private String bank_name;


    @Column(name = "account_num")
    private String accountNum;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "account_holder_info")
    private String accountHolderInfo;

    @Column(name = "last_used_date")
    private LocalDateTime lastUsedDate;
}
