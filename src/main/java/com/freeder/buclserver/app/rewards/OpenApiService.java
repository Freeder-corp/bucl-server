package com.freeder.buclserver.app.rewards;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

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

import com.freeder.buclserver.domain.openapi.dto.OpenApiAccessTokenDto;
import com.freeder.buclserver.domain.openapi.repository.AccessTokenRepository;
import com.freeder.buclserver.global.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OpenApiService {

	private final AccessTokenRepository accessTokenRepository;

	@Value("${openapi.client_id}")
	String clientId;

	@Value("${openapi.client_secret}")
	String clientSecret;

	public OpenApiService(AccessTokenRepository accessTokenRepository) {
		this.accessTokenRepository = accessTokenRepository;
	}

	@Transactional
	public void requestOpenApiAccessToken() {
		try {
			RestTemplate rest = new RestTemplate();

			URI uri = URI.create("https://testapi.openbanking.or.kr/oauth/2.0/token");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
			param.add("client_id", clientId);
			param.add("client_secret", clientSecret);
			param.add("scope", "oob");
			param.add("grant_type", "client_credentials");

			LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

			if (accessTokenRepository.findFirstByExpireDateAfter(now).isEmpty()) {
				OpenApiAccessTokenDto newAccessTokenRes;
				newAccessTokenRes = rest.postForObject(
					uri,
					new HttpEntity<>(param, headers),
					OpenApiAccessTokenDto.class
				);

				accessTokenRepository.save(newAccessTokenRes.toEntity());
				log.info("새로운 OpenAPI Access Token 발급 성공: {}", newAccessTokenRes.getAccess_token());
			} else {
				log.info("현재 유효한 OpenAPI Access Token이 있어 새로 발급하지 않습니다.");
			}
		} catch (Exception e) {
			log.error("OpenAPI Access Token 발급 실패", e);
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
		}
	}
}