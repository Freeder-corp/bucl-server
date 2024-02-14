package com.freeder.buclserver.domain.rewardwithdrawalaccount.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawalAccountDto {
	private String bank_code_std;
	private String bank_name;
	private String account_num;
	private String account_holder_name;
	private String account_holder_info;
}
