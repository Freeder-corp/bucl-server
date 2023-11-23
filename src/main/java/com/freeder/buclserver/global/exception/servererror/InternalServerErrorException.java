package com.freeder.buclserver.global.exception.servererror;

import org.springframework.http.HttpStatus;

import com.freeder.buclserver.global.exception.BaseException;

public class InternalServerErrorException extends BaseException {
	public InternalServerErrorException(String errorMessage) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
	}
}
