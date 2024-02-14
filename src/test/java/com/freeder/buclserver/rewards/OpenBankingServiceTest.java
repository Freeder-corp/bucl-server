package com.freeder.buclserver.rewards;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import com.freeder.buclserver.app.rewards.OpenBankingService;
import com.freeder.buclserver.domain.openbanking.dto.OpenBankingAccessTokenDto;
import com.freeder.buclserver.domain.openbanking.entity.OpenBankingAccessToken;
import com.freeder.buclserver.domain.openbanking.repository.AccessTokenRepository;

@DisplayName("액세스 토큰 발급 API")
@ExtendWith(MockitoExtension.class)

public class OpenBankingServiceTest {
	@Mock
	private AccessTokenRepository accessTokenRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private OpenBankingService openBankingService;

	@BeforeEach
	void setUp() {
		Field openBankingApiBaseUrlField = ReflectionUtils.findField(OpenBankingService.class,
			"openBankingApiBaseUrl");
		if (openBankingApiBaseUrlField != null) {
			openBankingApiBaseUrlField.setAccessible(true);
			ReflectionUtils.setField(openBankingApiBaseUrlField, openBankingService,
				"https://testapi.openbanking.or.kr");
		}

		Field clientIdField = ReflectionUtils.findField(OpenBankingService.class, "clientId");
		Field clientSecretField = ReflectionUtils.findField(OpenBankingService.class, "clientSecret");

		if (clientIdField != null && clientSecretField != null) {
			clientIdField.setAccessible(true);
			clientSecretField.setAccessible(true);

			ReflectionUtils.setField(clientIdField, openBankingService, "3d6b00d9-cac5-4d7f-8f2a-22adf8791a2d");
			ReflectionUtils.setField(clientSecretField, openBankingService, "531865df-1f25-4464-92fa-ec3231a6dfd2");
		}
	}

	@Test
	@DisplayName("기존 액세스 토큰이 있는 경우")
	void 액세스토큰조회_성공테스트() {
		// Given
		LocalDateTime now = LocalDateTime.now();
		OpenBankingAccessToken existingToken = new OpenBankingAccessToken(1L, "existing_token", "Bearer",
			now.plusDays(1).toString(), "scope", "client_use_code");
		when(accessTokenRepository.findFirstByExpireDateAfter(anyString())).thenReturn(Optional.of(existingToken));

		// When
		OpenBankingAccessTokenDto accessTokenDto = openBankingService.requestOpenApiAccessToken();

		// Then
		assertNotNull(accessTokenDto);
		assertEquals("existing_token", accessTokenDto.getAccess_token());
	}

	@Test
	@DisplayName("새로운 액세스 토큰을 발급하는 경우")
	void 액세스토큰발급_성공테스트() {
		// Given
		LocalDateTime now = LocalDateTime.now();
		when(accessTokenRepository.findFirstByExpireDateAfter(anyString())).thenReturn(Optional.empty());
		when(accessTokenRepository.findFirstByExpireDateBefore(anyString())).thenReturn(Optional.empty());

		OpenBankingAccessTokenDto newAccessTokenDto = new OpenBankingAccessTokenDto("new_token", "Bearer",
			now.plusDays(1).toString(), "scope", "client_use_code");
		ResponseEntity<OpenBankingAccessTokenDto> responseEntity = new ResponseEntity<>(newAccessTokenDto,
			HttpStatus.OK);

		// When
		OpenBankingAccessTokenDto accessTokenDto = openBankingService.requestOpenApiAccessToken();

		// Then
		assertNotNull(accessTokenDto);
		assertNotNull(accessTokenDto.getAccess_token());
	}

}
