package com.freeder.buclserver.global.exception.usershippingaddress;

import static org.springframework.http.HttpStatus.*;

import com.freeder.buclserver.global.exception.BaseException;

public class AddressUserNotMatchException extends BaseException {

	public AddressUserNotMatchException() {
		super(UNAUTHORIZED, UNAUTHORIZED.value(), "해당 주소지를 생성한 사용자가 아닙니다.");
	}
}
