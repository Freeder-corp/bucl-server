package com.freeder.buclserver.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.freeder.buclserver.global.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<?> handleRestApiException(BaseException error) {
		return new ResponseEntity<>(new ErrorResponse(error.getHttpStatus(), error.getErrorMessage()),
			error.getHttpStatus());
	}
}
