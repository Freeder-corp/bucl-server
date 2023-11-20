package com.freeder.buclserver.global.exception.kakao;

import org.springframework.http.HttpStatus;

import com.freeder.buclserver.global.exception.BaseException;

public class KakaoTokenValidException extends BaseException {

	public KakaoTokenValidException() {
		super(HttpStatus.UNAUTHORIZED, 401, "유효하지 않는 KAKAO 토큰입니다.");
	}
}
