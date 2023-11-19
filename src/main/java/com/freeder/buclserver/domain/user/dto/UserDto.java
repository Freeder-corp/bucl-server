package com.freeder.buclserver.domain.user.dto;

import java.time.LocalDateTime;

import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.vo.Gender;
import com.freeder.buclserver.domain.user.vo.JoinType;
import com.freeder.buclserver.domain.user.vo.Role;
import com.freeder.buclserver.domain.user.vo.UserGrade;
import com.freeder.buclserver.domain.user.vo.UserState;

public record UserDto(
	Long id,
	String email,
	String nickname,
	String hashedPw,
	String profilePath,
	Boolean isAlarmed,
	String cellPhone,
	Role role,
	JoinType joinType,
	UserState userState,
	UserGrade userGrade,
	Gender gender,
	LocalDateTime birthDate,
	String socialId,
	String refreshToken
) {

	public static UserDto from(User user) {
		return new UserDto(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			user.getHashedPw(),
			user.getProfilePath(),
			user.getIsAlarmed(),
			user.getCellPhone(),
			user.getRole(),
			user.getJoinType(),
			user.getUserState(),
			user.getUserGrade(),
			user.getGender(),
			user.getBirthDate(),
			user.getSocialId(),
			user.getRefreshToken());
	}
}
