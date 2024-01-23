package com.freeder.buclserver.global.openfeign.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.freeder.buclserver.app.auth.dto.response.KakaoUserInfo;

@FeignClient(
	name = "kakaouserinfo",
	url = "https://kapi.kakao.com",
	configuration = FeignClientConfiguration.class
)
public interface KakaoApiClient {

	@GetMapping("/v2/user/me")
	KakaoUserInfo getUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);
}
