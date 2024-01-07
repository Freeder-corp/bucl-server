package com.freeder.buclserver.global.exception.usershippingaddress;

import static org.springframework.http.HttpStatus.*;

import com.freeder.buclserver.global.exception.BaseException;

public class AlreadyDefaultAddressException extends BaseException {

	public AlreadyDefaultAddressException() {
		super(BAD_REQUEST, BAD_REQUEST.value(), "디폴트 주소로 변경하려는 주소는 이미 디폴트 주소입니다.");
	}
}
