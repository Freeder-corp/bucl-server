package com.freeder.buclserver.app.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.user.dto.UserDto;
import com.freeder.buclserver.domain.user.dto.response.MyProfileResponse;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.global.exception.auth.WithdrawalBadRequestException;
import com.freeder.buclserver.global.exception.user.UserIdNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final RewardRepository rewardRepository;

	@Transactional(readOnly = true)
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

		if (user.getRefreshToken() == null) {
			throw new WithdrawalBadRequestException();
		}

		user.withdrawal();
	}

	@Transactional(readOnly = true)
	public MyProfileResponse getMyProfile(Long userId) {
		return rewardRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId)
			.map(MyProfileResponse::from)
			.orElseGet(() -> getUserProfileWithoutReward(userId));
	}

	private MyProfileResponse getUserProfileWithoutReward(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserIdNotFoundException(userId));
		return MyProfileResponse.of(user.getProfilePath(), user.getNickname(), 0);
	}
}
