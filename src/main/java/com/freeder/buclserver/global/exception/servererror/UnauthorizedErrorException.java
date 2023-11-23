package com.freeder.buclserver.global.exception.servererror;

import org.springframework.http.HttpStatus;

import com.freeder.buclserver.global.exception.BaseException;

public class UnauthorizedErrorException extends BaseException {
	public UnauthorizedErrorException(String errorMessage) {
		super(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), errorMessage);
	}
}
