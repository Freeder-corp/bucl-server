package com.freeder.buclserver.app.utils;

import java.time.LocalDateTime;

import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.vo.Gender;
import com.freeder.buclserver.domain.user.vo.JoinType;
import com.freeder.buclserver.domain.user.vo.Role;
import com.freeder.buclserver.domain.user.vo.UserGrade;
import com.freeder.buclserver.domain.user.vo.UserState;

public class UserTestUtils {

	public static User createUser() {
		return User.builder()
			.email("email@gmail.com")
			.nickname("nickname")
			.profilePath("profile.png")
			.isAlarmed(Boolean.TRUE)
			.cellPhone("010-1234-5678")
			.role(Role.ROLE_USER)
			.joinType(JoinType.KAKAO)
			.userState(UserState.ACTIVE)
			.userGrade(UserGrade.BASIC)
			.gender(Gender.FEMALE)
			.birthDate(LocalDateTime.now())
			.socialId("1234567890")
			.refreshToken("refreshToken")
			.build();
	}

	public static User createLogoutUser() {
		return User.builder()
			.email("email@gmail.com")
			.nickname("nickname")
			.profilePath("profile.png")
			.isAlarmed(Boolean.TRUE)
			.cellPhone("010-1234-5678")
			.role(Role.ROLE_USER)
			.joinType(JoinType.KAKAO)
			.userState(UserState.ACTIVE)
			.userGrade(UserGrade.BASIC)
			.gender(Gender.FEMALE)
			.birthDate(LocalDateTime.now())
			.socialId("1234567890")
			.refreshToken(null)
			.build();
	}
}
