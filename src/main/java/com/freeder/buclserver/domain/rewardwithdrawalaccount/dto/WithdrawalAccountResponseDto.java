package com.freeder.buclserver.domain.rewardwithdrawalaccount.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WithdrawalAccountResponseDto {
	private String bankName;
	private String accountNum;

}
