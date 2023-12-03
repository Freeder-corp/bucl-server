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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenApiService {

	private final AccessTokenRepository accessTokenRepository;

	@Value("${openapi.client_id}")
	String clientId;

	@Value("${openapi.client_secret}")
	String clientSecret;

	@Transactional
	public void requestOpenApiAccessToken() {

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
			try {
				newAccessTokenRes = rest.postForObject(
					uri,
					new HttpEntity<>(param, headers),
					OpenApiAccessTokenDto.class
				);
			} catch (Exception e) {
				throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
			}
			accessTokenRepository.save(newAccessTokenRes.toEntity());
		}
	}
}