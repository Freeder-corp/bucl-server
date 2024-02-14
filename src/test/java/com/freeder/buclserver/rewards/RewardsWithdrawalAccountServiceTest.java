package com.freeder.buclserver.rewards;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import com.freeder.buclserver.app.rewards.RewardsWithdrawalAccountService;
import com.freeder.buclserver.domain.openbanking.entity.OpenBankingAccessToken;
import com.freeder.buclserver.domain.openbanking.repository.AccessTokenRepository;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.dto.WithdrawalAccountDto;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.entity.RewardWithdrawalAccount;
import com.freeder.buclserver.domain.rewardwithdrawalaccount.repository.RewardWithdrawalAccountRepository;
import com.freeder.buclserver.global.exception.BaseException;

@ExtendWith(MockitoExtension.class)
public class RewardsWithdrawalAccountServiceTest {

	@Mock
	private AccessTokenRepository accessTokenRepository;
	@Mock
	private RewardWithdrawalAccountRepository rewardWithdrawalAccountRepository;
	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private RewardsWithdrawalAccountService rewardsWithdrawalAccountService;

	@BeforeEach
	void setUp() {
		Field openBankingApiBaseUrlField = ReflectionUtils.findField(RewardsWithdrawalAccountService.class,
			"openBankingApiBaseUrl");
		if (openBankingApiBaseUrlField != null) {
			openBankingApiBaseUrlField.setAccessible(true);
			ReflectionUtils.setField(openBankingApiBaseUrlField, rewardsWithdrawalAccountService,
				"https://testapi.openbanking.or.kr");
		}
		String validAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJNMjAyMzAyNjkxIiwic2NvcGUiOlsic2EiXSwiaXNzIjoiaHR0cHM6Ly93d3cub3BlbmJhbmtpbmcub3Iua3IiLCJleHAiOjE3MTIwMzY5MjUsImp0aSI6IjRhZWE2M2Q3LTk4MzMtNDdmOC1iOTY4LTIzZTE4NTEyMTM0NSJ9.dd9ZuHt3O8E0b1y1u0a_Jrr38hUQhFIGxsC5q_QVjcE";
		String clientUseCode = "M202302691";
		OpenBankingAccessToken token = new OpenBankingAccessToken(1L, validAccessToken, "Bearer",
			LocalDateTime.now().plusDays(1).toString(), "read", clientUseCode);

		when(accessTokenRepository.findFirstByExpireDateAfter(anyString())).thenReturn(Optional.of(token));
		when(accessTokenRepository.findClientUseCodeByAccessToken(validAccessToken)).thenReturn(clientUseCode);
	}

	@Test
	@DisplayName("계좌 실명 조회 - 성공")
	void 계좌실명조회_성공테스트() {
		// Given
		Long userId = 11L;
		String bankName = "오픈뱅크";
		String bankCode = "097";
		String bankAccount = "1234567890123456";
		String realName = "홍길동";
		String birthday = "880101";

		when(rewardWithdrawalAccountRepository.findByUser_Id(anyLong()))
			.thenReturn(Optional.of(new RewardWithdrawalAccount()));

		WithdrawalAccountDto dto = new WithdrawalAccountDto();
		dto.setBank_code_std(bankCode);
		dto.setBank_name(bankName);
		dto.setAccount_num(bankAccount);
		dto.setAccount_holder_name(realName);
		dto.setAccount_holder_info(birthday);

		// When
		boolean result = rewardsWithdrawalAccountService.requestMatchAccountRealName(userId, bankName, bankAccount,
			realName, birthday);

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("계좌 실명 조회 - BaseException 발생")
	void 계좌실명조회_BaseException예외테스트() {
		// Given
		Long userId = 11L;
		String bankName = "오픈뱅크";
		String bankCode = "097";
		String bankAccount = "1234567890123456";
		String realName = "홍길동";
		String birthday = "880101";

		when(rewardWithdrawalAccountRepository.findByUser_Id(anyLong()))
			.thenThrow(BaseException.class);

		WithdrawalAccountDto dto = new WithdrawalAccountDto();
		dto.setBank_code_std(bankCode);
		dto.setBank_name(bankName);
		dto.setAccount_num(bankAccount);
		dto.setAccount_holder_name(realName);
		dto.setAccount_holder_info(birthday);

		// When and Then
		assertThrows(BaseException.class, () -> {
			rewardsWithdrawalAccountService.requestMatchAccountRealName(userId, bankName, bankAccount,
				realName, birthday);
		});
	}
}
