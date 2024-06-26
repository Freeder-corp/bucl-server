package com.freeder.buclserver.domain.openbanking.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BANK_CODE {
	KDB산업은행("002"), SC제일은행("023"), 전북은행("037"),
	IBK기업은행("003"), 한국씨티은행("027"), 경남은행("039"),
	KB국민은행("004"), 대구은행("031"), 하나은행("081"),
	수협은행("007"), 부산은행("032"), 신한은행("088"),
	NH농협은행("011"), 광주은행("034"), 케이뱅크("089"),
	우리은행("020"), 제주은행("035"), 카카오뱅크("090"), 오픈뱅크("097");

	private final String bankCode;

	public static String getCodeByBankName(String bankName) {
		for (BANK_CODE bankCode : values()) {
			if (bankCode.name().equalsIgnoreCase(bankName)) {
				return bankCode.getBankCode();
			}
		}
		throw new IllegalArgumentException("Invalid bank name: " + bankName);
	}

}
