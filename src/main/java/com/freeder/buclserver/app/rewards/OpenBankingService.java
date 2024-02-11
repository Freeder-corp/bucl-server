package com.freeder.buclserver.app.rewards;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;

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

import com.freeder.buclserver.domain.openbanking.dto.OpenBankingAccessTokenDto;
import com.freeder.buclserver.domain.openbanking.entity.OpenBankingAccessToken;
import com.freeder.buclserver.domain.openbanking.repository.AccessTokenRepository;
import com.freeder.buclserver.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenBankingService {

	private final AccessTokenRepository accessTokenRepository;

	@Value("${openbanking.client_id}")
	private String clientId;

	@Value("${openbanking.client_secret}")
	private String clientSecret;

	@Value("${openbanking.api.base-url}")
	private String openBankingApiBaseUrl;

	@Transactional
	public OpenBankingAccessTokenDto requestOpenApiAccessToken() {
		try {
			RestTemplate rest = new RestTemplate();
			URI uri = URI.create(openBankingApiBaseUrl + "/oauth/2.0/token");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			body.add("client_id", clientId);
			body.add("client_secret", clientSecret);
			body.add("scope", "sa");
			body.add("grant_type", "client_credentials");

			LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
			Optional<OpenBankingAccessToken> existingToken = accessTokenRepository.findFirstByExpireDateAfter(
				now.toString());

			if (existingToken.isPresent()) {
				return mapToDto(existingToken);
			} else {
				Optional<OpenBankingAccessToken> expiredToken = accessTokenRepository.findFirstByExpireDateBefore(
					now.toString());

				if (expiredToken.isPresent()) {
					OpenBankingAccessTokenDto refreshedAccessTokenRes;
					try {
						refreshedAccessTokenRes = rest.postForObject(
							uri,
							new HttpEntity<>(body, headers),
							OpenBankingAccessTokenDto.class
						);
					} catch (Exception e) {
						throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
					}

					OpenBankingAccessToken expiredTokenEntity = expiredToken.get();
					expiredTokenEntity.updateAccessToken(refreshedAccessTokenRes.getAccess_token());
					expiredTokenEntity.updateExpireDate(refreshedAccessTokenRes.getExpires_in());

					accessTokenRepository.save(expiredTokenEntity);

					return refreshedAccessTokenRes;
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
			}
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
		}
	}

	private OpenBankingAccessTokenDto mapToDto(Optional<OpenBankingAccessToken> optionalToken) {
		OpenBankingAccessToken token = optionalToken.orElseThrow(() ->
			new BaseException(HttpStatus.NOT_FOUND, 404, "액세스 토큰을 찾을 수 없습니다."));

		return new OpenBankingAccessTokenDto(
			token.getAccessToken(),
			token.getTokenType(),
			token.getExpireDate(),
			token.getScope(),
			token.getClientUseCode()
		);
	}
}
