package com.freeder.buclserver.app.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.app.auth.dto.response.TokenResponse;
import com.freeder.buclserver.app.auth.exception.LogoutUserWithdrawalException;
import com.freeder.buclserver.app.auth.exception.RefreshTokenNotFoundException;
import com.freeder.buclserver.core.security.JwtTokenProvider;
import com.freeder.buclserver.domain.user.dto.UserDto;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.exception.UserIdNotFoundException;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.domain.user.vo.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;

	public Optional<UserDto> findBySocialId(String socialUid) {
		return userRepository.findBySocialId(socialUid)
			.map(UserDto::from);
	}

	@Transactional
	public UserDto join(UserDto userDto) {
		User user = userRepository.save(userDto.toEntity());
		return UserDto.from(user);
	}

	@Transactional
	public void rejoin(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		user.rejoin();
	}

	@Transactional
	public TokenResponse createJwtTokens(Long userId, Role role) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		String accessToken = jwtTokenProvider.createAccessToken(userId, role);
		String refreshToken = jwtTokenProvider.createRefreshToken(userId, role);

		user.updateRefreshToken(refreshToken);

		return TokenResponse.of(accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse renewTokens(String refreshToken) {
		jwtTokenProvider.validateToken(refreshToken);

		User user = userRepository.findByRefreshToken(refreshToken)
			.orElseThrow(RefreshTokenNotFoundException::new);

		user.deleteRefreshToken();

		return createJwtTokens(
			user.getId(),
			Role.valueOf(jwtTokenProvider.getUserRole(refreshToken))
		);
	}

	@Transactional
	public void deleteRefreshToken(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		user.deleteRefreshToken();
	}

	@Transactional
	public void withdrawal(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		if (user.getRefreshToken() == null) {
			throw new LogoutUserWithdrawalException();
		}

		user.withdrawal();
	}
}
