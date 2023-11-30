package com.freeder.buclserver.domain.user.dto.response;

import com.freeder.buclserver.domain.reward.entity.Reward;

public record MyProfileResponse(
	String profilePath,
	String nickname,
	Integer rewardSum
) {

	public static MyProfileResponse of(String profilePath, String nickname, Integer rewardSum) {
		return new MyProfileResponse(profilePath, nickname, rewardSum);
	}

	public static MyProfileResponse from(Reward reward) {
		return MyProfileResponse.of(
			reward.getUser().getProfilePath(),
			reward.getUser().getNickname(),
			reward.getRewardSum()
		);
	}
}
