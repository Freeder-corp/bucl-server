package com.freeder.buclserver.domain.rewardwithdrawalaccount.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.rewardwithdrawalaccount.entity.RewardWithdrawalAccount;

@Repository
public interface RewardWithdrawalAccountRepository extends JpaRepository<RewardWithdrawalAccount, Long> {

	Optional<RewardWithdrawalAccount> findByUser_Id(Long userId);
}
