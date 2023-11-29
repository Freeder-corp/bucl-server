package com.freeder.buclserver.domain.user.dto.response;

import com.freeder.buclserver.domain.reward.entity.Reward;

public record UserProfileResponse(
	String profilePath,
	String nickname,
	Integer rewardSum
) {

	public static UserProfileResponse of(String profilePath, String nickname, Integer rewardSum) {
		return new UserProfileResponse(profilePath, nickname, rewardSum);
	}

	public static UserProfileResponse from(Reward reward) {
		return UserProfileResponse.of(
			reward.getUser().getProfilePath(),
			reward.getUser().getNickname(),
			reward.getRewardSum()
		);
	}
}
