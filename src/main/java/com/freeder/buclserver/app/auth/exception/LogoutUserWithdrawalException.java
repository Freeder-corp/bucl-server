package com.freeder.buclserver.app.auth.exception;

import static org.springframework.http.HttpStatus.*;

import com.freeder.buclserver.global.exception.BaseException;

public class LogoutUserWithdrawalException extends BaseException {

	public LogoutUserWithdrawalException() {
		super(BAD_REQUEST, BAD_REQUEST.value(), "로그아웃한 사용자는 탈퇴를 할 수 없습니다.");
	}
}
