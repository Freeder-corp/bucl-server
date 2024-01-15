package com.freeder.buclserver.app.rewards;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.freeder.buclserver.domain.openbanking.entity.OpenBankingAccessToken;
import com.freeder.buclserver.domain.openbanking.repository.AccessTokenRepository;
import com.freeder.buclserver.domain.openbanking.vo.BANK_CODE;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.dto.WithdrawalAccountDto;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.dto.WithdrawalAccountResponseDto;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.entity.RewardWithdrawalAccount;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.repository.RewardWithdrawalAccountRepository;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.global.exception.BaseException;
import com.nimbusds.jose.shaded.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RewardsWithdrawalAccountService {

	private final AccessTokenRepository accessTokenRepository;
	private final RewardWithdrawalAccountRepository rewardWithdrawalAccountRepository;
	@Value("${openbanking.api.base-url}")
	private String openBankingApiBaseUrl;

	public RewardsWithdrawalAccountService(AccessTokenRepository accessTokenRepository,
		RewardWithdrawalAccountRepository rewardWithdrawalAccountRepository) {
		this.accessTokenRepository = accessTokenRepository;
		this.rewardWithdrawalAccountRepository = rewardWithdrawalAccountRepository;
	}

	@Transactional
	public boolean requestMatchAccountRealName(Long userId, String bankCode, String bankAccount, String realName,
		String birthday) {
		if (birthday.length() != 6 || bankAccount.length() > 16)
			return false;

		RestTemplate rest = new RestTemplate();
		URI uri = URI.create(openBankingApiBaseUrl + "/v2.0/inquiry/real_name");
		System.out.println("uri = " + uri);
		HttpHeaders headers = new HttpHeaders();
		String accessToken = accessTokenRepository.findFirstByExpireDateAfter(
				LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString())
			.map(OpenBankingAccessToken::getAccessToken)
			.orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, 404, "액세스 토큰을 찾을 수 없습니다."));

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);
		headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		JSONObject param = new JSONObject();
		String uniqueNum = String.valueOf(System.currentTimeMillis() % 1000000000);

		String clientUseCode = accessTokenRepository.findClientUseCodeByAccessToken(accessToken);
		param.put("bank_tran_id", clientUseCode + "U" + uniqueNum);
		param.put("bank_code_std", BANK_CODE.getCodeByBankName(bankCode));
		param.put("account_num", bankAccount);
		param.put("account_holder_info_type", "");
		param.put("account_holder_info", birthday);
		param.put("tran_dtime",
			LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

		WithdrawalAccountDto realNameDto;
		try {
			realNameDto = rest.postForObject(uri, new HttpEntity<>(param.toJSONString(), headers),
				WithdrawalAccountDto.class);

		} catch (Exception e) {
			log.error("Error during RestTemplate call: ", e);
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류입니다: " + e.getMessage());
		}

		if (realNameDto == null) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류: 응답 값이 null입니다.");
		}

		if (!realNameDto.getBank_code_std().equals(BANK_CODE.getCodeByBankName(bankCode))) {
			throw new BaseException(HttpStatus.BAD_REQUEST, 400, "계좌 오류: 은행이 일치하지 않습니다");
		}

		// if (!realNameDto.getAccount_holder_name().equals(realName)) {
		// throw new BaseException(HttpStatus.BAD_REQUEST, 400, "계좌 오류: 이름이 일치하지 않습니다.");
		// }

		if (!realNameDto.getAccount_holder_info().equals(birthday)) {
			throw new BaseException(HttpStatus.BAD_REQUEST, 400, "계좌 오류: 생년월일이 일치하지 않습니다.");
		}

		System.out.println("userId = " + userId);
		RewardWithdrawalAccount withdrawalAccount = rewardWithdrawalAccountRepository.findByUser_Id(userId)
			.orElse(new RewardWithdrawalAccount());
		withdrawalAccount.setUser(new User(userId));
		withdrawalAccount.setBankCodeStd(realNameDto.getBank_code_std());
		withdrawalAccount.setBankName(realNameDto.getBank_name());
		withdrawalAccount.setAccountNum(realNameDto.getAccount_num());
		withdrawalAccount.setAccountHolderName(realNameDto.getAccount_holder_name());
		withdrawalAccount.setAccountHolderInfo(realNameDto.getAccount_holder_info());
		withdrawalAccount.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

		rewardWithdrawalAccountRepository.save(withdrawalAccount);
		return true;
	}

	@Transactional
	public WithdrawalAccountResponseDto getWithdrawalAccountByUserId(Long userId) {
		Optional<RewardWithdrawalAccount> withdrawalAccountOptional = rewardWithdrawalAccountRepository.findByUser_Id(
			userId);

		if (withdrawalAccountOptional.isPresent()) {
			RewardWithdrawalAccount withdrawalAccount = withdrawalAccountOptional.get();
			WithdrawalAccountResponseDto responseDto = new WithdrawalAccountResponseDto();
			responseDto.setBank_name(withdrawalAccount.getBankName());
			responseDto.setAccount_num(withdrawalAccount.getAccountNum());
			return responseDto;
		} else {
			throw new BaseException(HttpStatus.NOT_FOUND, 404, "등록된 계좌가 없습니다: " + userId);
		}
	}

}
