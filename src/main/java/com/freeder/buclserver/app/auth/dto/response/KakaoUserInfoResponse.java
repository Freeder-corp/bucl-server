package com.freeder.buclserver.app.auth.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.freeder.buclserver.domain.user.dto.UserDto;
import com.freeder.buclserver.domain.user.vo.Gender;
import com.freeder.buclserver.domain.user.vo.JoinType;
import com.freeder.buclserver.domain.user.vo.Role;
import com.freeder.buclserver.domain.user.vo.UserGrade;
import com.freeder.buclserver.domain.user.vo.UserState;

import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoUserInfoResponse {

	private String id;
	private LocalDateTime connectedAt;
	private KakaoAccount kakaoAccount;

	@Getter
	static class KakaoAccount {

		private Profile profile;
		private String email;
		private String gender;

		@Getter
		@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
		static class Profile {
			private String nickname;
			private String profileImageUrl;
		}
	}

	public UserDto toUserDto() {
		return UserDto.of(
			null,
			this.kakaoAccount.email,
			this.kakaoAccount.profile.nickname,
			this.kakaoAccount.profile.profileImageUrl,
			null,
			null,
			Role.ROLE_USER,
			JoinType.KAKAO,
			UserState.ACTIVE,
			UserGrade.BASIC,
			Gender.parseGenderEnum(this.kakaoAccount.gender),
			null,
			this.id,
			null,
			null
		);
	}
}