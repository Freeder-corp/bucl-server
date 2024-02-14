package com.freeder.buclserver.app.payment;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.payment.dto.PaymentPrepareDto;
import com.freeder.buclserver.app.payment.dto.PaymentVerifyDto;
import com.freeder.buclserver.global.exception.servererror.BadRequestErrorException;
import com.freeder.buclserver.global.response.BaseResponse;
import com.siot.IamportRestClient.response.Payment;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "payment 관련 API", description = "결제 관련  API")
@Slf4j
public class PaymentController {

	private final PaymentService paymentService;

	private Long userId = 1L;

	@PostMapping("/preparation")
	public BaseResponse<PaymentPrepareDto> preparePayment(@RequestBody PaymentPrepareDto paymentPrepareDto) {
		paymentService.preparePayment(paymentPrepareDto);
		return new BaseResponse<>(paymentPrepareDto, HttpStatus.OK, "사전 검증 됐습니다.");
	}

	@PostMapping("/verification")
	public BaseResponse<Payment> verifyPayment(
		@ModelAttribute @Valid PaymentVerifyDto paymentVerifyDto,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult.getAllErrors());
			log.info("{\"status\":\"error\", \"msg\":\""
				+ "요청 데이터가 인위적으로 바뀌어서 결제가 취소 되었습니다."
				+ "\", \"cause\":\"" + bindingResult.getAllErrors().toString() + "\"}");
			String impUid = paymentVerifyDto.getImpUid();
			paymentService.cancelPayment(impUid);
			throw new BadRequestErrorException("요청 데이터가 인위적으로 바뀌어서 결제가 취소되었습니다.");
		}
		return new BaseResponse<>(paymentService.verifyPayment(userId, paymentVerifyDto).getResponse(),
			HttpStatus.OK,
			"결제 되었습니다.");
	}
}
