package com.freeder.buclserver.domain.rewardwithdrawal.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawalHistoryDto {
	private Integer rewardWithdrawalAmount;
	private LocalDateTime lastUsedDate;
}
