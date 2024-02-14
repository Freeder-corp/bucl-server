package com.freeder.buclserver.app.rewards.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PutWithdrawalAccountReq {
	private String bankName;
	private String accountNum;
}
