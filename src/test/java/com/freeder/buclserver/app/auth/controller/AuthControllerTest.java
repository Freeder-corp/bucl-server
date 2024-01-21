// package com.freeder.buclserver.app.auth.controller;
//
// import static org.mockito.BDDMockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.Optional;
//
// import javax.servlet.http.Cookie;
//
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.freeder.buclserver.app.auth.dto.request.KakaoLoginRequest;
// import com.freeder.buclserver.app.auth.dto.response.KakaoUserInfoResponse;
// import com.freeder.buclserver.app.auth.service.AuthService;
// import com.freeder.buclserver.core.config.SecurityConfig;
// import com.freeder.buclserver.core.security.CustomUserDetails;
// import com.freeder.buclserver.core.security.JwtAccessDeniedHandler;
// import com.freeder.buclserver.core.security.JwtAuthenticationEntryPoint;
// import com.freeder.buclserver.core.security.JwtAuthenticationFilter;
// import com.freeder.buclserver.core.security.JwtExceptionFilter;
// import com.freeder.buclserver.core.security.JwtTokenProvider;
// import com.freeder.buclserver.domain.user.dto.UserDto;
// import com.freeder.buclserver.domain.user.vo.Role;
// import com.freeder.buclserver.global.openfeign.kakao.KakaoApiClient;
//
// @Import({
// 	SecurityConfig.class,
// 	JwtAccessDeniedHandler.class,
// 	JwtAuthenticationEntryPoint.class,
// 	JwtExceptionFilter.class,
// 	JwtAuthenticationFilter.class,
// 	JwtTokenProvider.class
// })
// @EnableConfigurationProperties
// @MockBean(JpaMetamodelMappingContext.class)
// @ActiveProfiles("test")
// @WebMvcTest(controllers = AuthController.class)
// class AuthControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@MockBean
// 	private AuthService authService;
//
// 	@MockBean
// 	private KakaoApiClient kakaoApiClient;
//
// 	@Value("${bucl.service.auth.COOKIE-MAX-AGE-REFRESH_TOKEN}")
// 	private int COOKIE_MAX_AGE_REFRESH_TOKEN;
//
// 	private final String REFRESH_TOKEN_PREFIX = "refresh_token";
//
// 	@Test
// 	void 카카오_로그인을_하고_jwt_토큰_반환할_때_accessToken은_body에_refreshToken은_cookie에_담아서_반환한다() throws Exception {
// 		// given
// 		KakaoLoginRequest request = new KakaoLoginRequest("kakao-login-token");
// 		given(kakaoApiClient.getUserInfo(anyString())).willReturn(any(KakaoUserInfoResponse.class));
// 		given(authService.findBySocialId(anyString())).willReturn(Optional.of(any(UserDto.class)));
//
// 		// when & then
// 		mockMvc.perform(
// 				post("/api/v1/auth/login/kakao")
// 					.content(objectMapper.writeValueAsString(request))
// 					.contentType(MediaType.APPLICATION_JSON)
// 			)
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.data").isNotEmpty())
// 			.andExpect(cookie().exists(REFRESH_TOKEN_PREFIX))
// 			.andExpect(cookie().maxAge(REFRESH_TOKEN_PREFIX, COOKIE_MAX_AGE_REFRESH_TOKEN))
// 			.andDo(print());
// 	}
//
// 	@Test
// 	void 재발급한_토큰_반환할_때_accessToken은_body에_refreshToken은_cookie에_담아서_반환한다() throws Exception {
// 		// given
// 		KakaoLoginRequest request = new KakaoLoginRequest("KakaoLoginRequest");
//
// 		// when & then
// 		MvcResult mvcResult = mockMvc.perform(
// 				post("/api/v1/auth/login/kakao")
// 					.content(objectMapper.writeValueAsString(request))
// 					.contentType(MediaType.APPLICATION_JSON)
// 			)
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.data").isNotEmpty())
// 			.andReturn();
//
// 		Cookie cookie = mvcResult.getResponse().getCookie("refresh-token");
// 		Assertions.assertThat(cookie).isNotNull();
// 	}
//
// 	private CustomUserDetails createTestUser() {
// 		return CustomUserDetails.of("1L", String.valueOf(Role.ROLE_USER));
// 	}
// }