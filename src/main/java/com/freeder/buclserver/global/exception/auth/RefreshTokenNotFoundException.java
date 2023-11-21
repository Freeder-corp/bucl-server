package com.freeder.buclserver.global.exception.auth;

import org.springframework.http.HttpStatus;

import com.freeder.buclserver.global.exception.BaseException;

public class RefreshTokenNotFoundException extends BaseException {

	public RefreshTokenNotFoundException() {
		super(HttpStatus.NOT_FOUND, 404, "해당 refresh 토큰을 가진 사용자가 없습니다.");
	}
}
