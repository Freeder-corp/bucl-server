package com.freeder.buclserver.global.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.freeder.buclserver.global.response.ErrorResponse;
import com.siot.IamportRestClient.exception.IamportResponseException;

@RestControllerAdvice
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

	@ExceptionHandler(IamportResponseException.class)
	public ResponseEntity<?> handleIamportResponseException(IamportResponseException error) {
		return new ResponseEntity<>(
			new ErrorResponse(HttpStatus.valueOf(error.getHttpStatusCode()), "결제 api에서 오류가 발생했습니다.").getStatusCode());
	}
}
