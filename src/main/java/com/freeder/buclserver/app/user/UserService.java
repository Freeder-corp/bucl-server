package com.freeder.buclserver.app.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.user.dto.UserDto;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.global.exception.user.UserIdNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

	private final UserRepository userRepository;

	public Optional<UserDto> findBySocialIdAndDeletedAtIsNull(String socialUid) {
		return userRepository.findBySocialIdAndDeletedAtIsNull(socialUid)
			.map(UserDto::from);
	}

	@Transactional
	public UserDto join(UserDto userDto) {
		User user = userRepository.save(userDto.toEntity());
		return UserDto.from(user);
	}

	@Transactional
	public void deleteRefreshToken(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		user.deleteRefreshToken();
	}

	@Transactional
	public void withdrawal(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		user.withdrawal();
	}
}
