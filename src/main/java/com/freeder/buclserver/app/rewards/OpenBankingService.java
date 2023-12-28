package com.freeder.buclserver.app.rewards;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.freeder.buclserver.domain.openbanking.dto.OpenBankingAccessTokenDto;
import com.freeder.buclserver.domain.openbanking.entity.OpenBankingAccessToken;
import com.freeder.buclserver.domain.openbanking.repository.AccessTokenRepository;
import com.freeder.buclserver.global.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OpenBankingService {

	private final AccessTokenRepository accessTokenRepository;

	@Value("${openbanking.client_id}")
	private String clientId;

	@Value("${openbanking.client_secret}")
	private String clientSecret;

	@Value("${openbanking.api.base-url}")
	private String openBankingApiBaseUrl;

	@Value("${openbanking.redirect.uri}")
	private String redirectUri;

	public OpenBankingService(AccessTokenRepository accessTokenRepository) {
		this.accessTokenRepository = accessTokenRepository;
	}

	@Transactional
	public void requestOpenApiUserCertification() {
		try {
			String stateValue = UUID.randomUUID().toString().replace("-", "");

			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
					openBankingApiBaseUrl + "/oauth/2.0/authorize")
				.queryParam("response_type", "code")
				.queryParam("client_id", clientId)
				.queryParam("redirect_uri", redirectUri)
				.queryParam("scope", "login inquiry transfer")
				.queryParam("state", stateValue)
				.queryParam("auth_type", "0");

			// 사용자를 리디렉션하기 위한 URL
			String authorizationUrl = builder.toUriString();

			System.out.println("authorizationUrl = " + authorizationUrl);
		} catch (Exception e) {
			throw new RuntimeException("Open Banking API 인증 요청 중 오류 발생", e);
		}
	}

	@Transactional
	public OpenBankingAccessTokenDto requestOpenApiAccessToken(String code) {
		try {
			RestTemplate rest = new RestTemplate();
			URI uri = URI.create(openBankingApiBaseUrl + "/oauth/2.0/token");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			body.add("client_id", clientId);
			body.add("client_secret", clientSecret);
			body.add("code", code);
			body.add("scope", "oob");
			body.add("grant_type", "client_credentials");

			String now = LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString();

			Optional<OpenBankingAccessToken> existingToken = accessTokenRepository.findFirstByExpireDateAfter(now);
			if (existingToken.isPresent()) {
				return mapToDto(existingToken.get());
			} else {
				OpenBankingAccessTokenDto newAccessTokenRes;
				try {
					newAccessTokenRes = rest.postForObject(
						uri,
						new HttpEntity<>(body, headers),
						OpenBankingAccessTokenDto.class
					);
				} catch (Exception e) {
					throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
				}
				accessTokenRepository.save(newAccessTokenRes.toEntity());
				return newAccessTokenRes;
			}
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
		}
	}

	private OpenBankingAccessTokenDto mapToDto(OpenBankingAccessToken token) {
		return new OpenBankingAccessTokenDto(
			token.getAccessToken(),
			token.getTokenType(),
			token.getExpireDate(),
			token.getScope(),
			token.getClientUseCode()
		);
	}

}
