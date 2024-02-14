package com.freeder.buclserver.app.rewards.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAccountRealNameReq {
	private String bankCode;
	private String bankAccount;
	private String realName;
	private String birthday;
}
