package com.freeder.buclserver.app.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freeder.buclserver.app.auth.dto.response.TokenResponse;
import com.freeder.buclserver.app.auth.exception.LogoutUserWithdrawalException;
import com.freeder.buclserver.core.security.JwtTokenProvider;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.domain.user.vo.Role;
import com.freeder.buclserver.domain.user.vo.UserGrade;
import com.freeder.buclserver.domain.user.vo.UserState;
import com.freeder.buclserver.util.UserTestUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private UserRepository userRepository;

	@Test
	void 탈퇴한_회원이_로그인을_시도한다면_재가입_로직을_실행한다() {
		// given
		Long userId = 1L;
		User user = UserTestUtil.createWthdrawalUser();
		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		// when
		authService.rejoin(userId);

		// then
		assertThat(user.getUserState()).isEqualTo(UserState.ACTIVE);
		assertThat(user.getUserGrade()).isEqualTo(UserGrade.BASIC);
	}

	@Test
	void 사용자의_PK와_역할_정보를_받아_액세스_토큰과_리프레시_토큰_생성하고_리프레시_토큰을_DB에_저장_후_토큰_값을_반환한다() {
		// given
		Long userId = 1L;
		Role role = Role.ROLE_USER;
		String expectAccessToken = "testAccessToken";
		String expectRefreshToken = "testRefreshToken";
		User user = UserTestUtil.create();
		given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
		given(jwtTokenProvider.createAccessToken(userId, role)).willReturn(expectAccessToken);
		given(jwtTokenProvider.createRefreshToken(userId, role)).willReturn(expectRefreshToken);

		// when
		TokenResponse actualTokenResponse = authService.createJwtTokens(userId, role);

		// then
		then(jwtTokenProvider).should().createAccessToken(anyLong(), any(Role.class));
		then(jwtTokenProvider).should().createRefreshToken(anyLong(), any(Role.class));
		assertThat(actualTokenResponse.accessToken()).isEqualTo(expectAccessToken);
		assertThat(actualTokenResponse.refreshToken()).isEqualTo(expectRefreshToken);
		assertThat(user.getRefreshToken()).isEqualTo(expectRefreshToken);
	}

	@Test
	void 회원의_PK를_받아_리프레시_토큰을_삭제해_로그아웃을_진행한다() {
		// given
		User user = UserTestUtil.create();
		given(userRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(user));

		// when
		authService.deleteRefreshToken(anyLong());

		// then
		assertThat(user.getRefreshToken()).isNull();
	}

	@Test
	void 회원의_PK를_받아_회원탈퇴를_진행한다() {
		// given
		User user = UserTestUtil.create();
		given(userRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(user));

		// when
		authService.withdrawal(anyLong());

		// then
		assertThat(user.getRefreshToken()).isNull();
		assertThat(user.getUserState()).isEqualTo(UserState.DELETED);
		assertThat(user.getDeletedAt()).isNotNull();
	}

	@Test
	void 로그아웃한_사용자가_회원탈퇴를_요청하면_에러가_발생한다() {
		// given
		User user = UserTestUtil.createLogoutUser();
		given(userRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(user));

		// when
		Throwable throwable = catchThrowable(() -> authService.withdrawal(anyLong()));

		// then
		assertThat(throwable).isInstanceOf(LogoutUserWithdrawalException.class);
	}
}