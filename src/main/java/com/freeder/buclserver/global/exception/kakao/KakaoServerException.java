package com.freeder.buclserver.global.exception.kakao;

import org.springframework.http.HttpStatus;

import com.freeder.buclserver.global.exception.BaseException;

public class KakaoServerException extends BaseException {

	public KakaoServerException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, 500, "카카오 서버와 통신 중 에러가 발생했습니다.");
	}
}
