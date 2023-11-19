package com.freeder.buclserver.app.payment;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.app.payment.dto.PaymentPrepareDto;
import com.freeder.buclserver.app.payment.dto.PaymentVerifyDto;
import com.freeder.buclserver.global.exception.BaseException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

@Service
public class PaymentService {
	private int testPrice = 1300;

	public boolean preparePayment(IamportClient iamportClient, PaymentPrepareDto paymentPrepareDto) {
		PrepareData prepareData = new PrepareData(paymentPrepareDto.getMerchantUid(),
			BigDecimal.valueOf(paymentPrepareDto.getAmount()));
		try {
			iamportClient.postPrepare(prepareData);
			return true;
		} catch (Exception error) {
			throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "사전 검증 요청 실패");
		}
	}

	public IamportResponse<Payment> verifyPayment(IamportClient iamportClient,
		PaymentVerifyDto paymentVerifyDto) throws
		IamportResponseException,
		IOException {
		String impUid = paymentVerifyDto.getImpUid();
		int amount = paymentVerifyDto.getAmount();
		IamportResponse<Payment> irsp = iamportClient.paymentByImpUid(impUid);

		int portoneAmount = irsp.getResponse().getAmount().intValue();

		if (portoneAmount != amount) {
			CancelData cancelData = cancelPayment(iamportClient, irsp);
			throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
				"요청 결제 금액과 결제 금액이 다릅니다.");
		}
		if (testPrice != amount) {
			CancelData cancelData = cancelPayment(iamportClient, irsp);
			throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
				"결제 금액과 실제 결제 해야될 금액과 일치 하지 않습니다.");
		}

		return irsp;
	}

	public CancelData cancelPayment(IamportClient iamportClient, IamportResponse<Payment> irsp) throws
		IamportResponseException,
		IOException {
		CancelData cancelData = new CancelData(irsp.getResponse().getImpUid(), true);
		iamportClient.cancelPaymentByImpUid(cancelData);
		return cancelData;
	}
}
