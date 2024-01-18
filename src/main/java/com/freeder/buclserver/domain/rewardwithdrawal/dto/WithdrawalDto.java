package com.freeder.buclserver.domain.rewardwithdrawal.dto;

import java.time.LocalDateTime;

import com.freeder.buclserver.domain.rewardwithdrawal.vo.WithdrawalStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawalDto {
	private Integer rewardWithdrawalAmount;
	private WithdrawalStatus withdrawalStatus;
	private LocalDateTime lastUsedDate;
}
