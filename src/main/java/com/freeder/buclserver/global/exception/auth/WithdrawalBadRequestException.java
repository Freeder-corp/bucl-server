package com.freeder.buclserver.global.exception.auth;

import static org.springframework.http.HttpStatus.*;

import com.freeder.buclserver.global.exception.BaseException;

public class WithdrawalBadRequestException extends BaseException {

	public WithdrawalBadRequestException() {
		super(BAD_REQUEST, BAD_REQUEST.value(), "로그아웃한 사용자는 탈퇴를 할 수 없습니다.");
	}
}
