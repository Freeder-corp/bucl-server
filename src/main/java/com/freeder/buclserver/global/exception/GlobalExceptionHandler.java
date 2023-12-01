package com.freeder.buclserver.global.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.freeder.buclserver.global.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<?> handleRestApiException(BaseException error) {
		return new ResponseEntity<>(new ErrorResponse(error.getHttpStatus(), error.getErrorMessage()),
			error.getHttpStatus());
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> handleIoException(IOException error) {
		return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "").getStatusCode());
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<?> handleNullPointerException(NullPointerException error) {
		log.info(error.getMessage() + error);
		return new ResponseEntity<>(
			new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버상에서 문제가 발생했습니다.").getStatusCode());
	}
}
