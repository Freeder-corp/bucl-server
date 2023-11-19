package com.freeder.buclserver.app.payment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.payment.dto.PaymentPrepareDto;
import com.freeder.buclserver.app.payment.dto.PaymentVerifyDto;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "payment 관련 API", description = "결제 관련  API")
public class PaymentController {
	private int testPrice = 1300;

	@Value("${iamport.key}")
	private String restApiKey;
	@Value("${iamport.secret}")
	private String restApiSecret;

	private IamportClient iamportClient;

	private final PaymentService paymentService;

	@PostConstruct
	public void init() {
		this.iamportClient = new IamportClient(restApiKey, restApiSecret);
	}

	@GetMapping("/prepare")
	public String testPrepare() {
		return "gggg";
	}

	@PostMapping("/preparation")
	public PaymentPrepareDto preparePayment(@RequestBody PaymentPrepareDto paymentPrepareDto) {
		boolean isVerified = paymentService.preparePayment(iamportClient, paymentPrepareDto);

		return paymentPrepareDto;
	}

	@PostMapping("/verification")
	public IamportResponse<Payment> verifyPayment(@RequestBody PaymentVerifyDto paymentVerifyDto) throws Exception {

		return paymentService.verifyPayment(iamportClient, paymentVerifyDto);
	}
}
