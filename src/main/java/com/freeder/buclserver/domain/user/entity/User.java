package com.freeder.buclserver.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.freeder.buclserver.domain.affiliate.entity.Affiliate;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.rewardwithdrawal.entity.RewardWithdrawal;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.user.util.ProfileImage;
import com.freeder.buclserver.domain.user.vo.Gender;
import com.freeder.buclserver.domain.user.vo.JoinType;
import com.freeder.buclserver.domain.user.vo.Role;
import com.freeder.buclserver.domain.user.vo.UserGrade;
import com.freeder.buclserver.domain.user.vo.UserState;
import com.freeder.buclserver.domain.wish.entity.Wish;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimestampMixin {
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "user")
	private List<Wish> wishes = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Reward> rewards = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<ProductReview> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Affiliate> affiliates = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<ShippingAddress> shippingAddresses = new ArrayList<>();

	@OneToMany(mappedBy = "consumer")
	private List<ConsumerOrder> consumerOrders = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<RewardWithdrawal> rewardWithdrawals = new ArrayList<>();

	@Column(length = 320)
	private String email; // 이메일
	private String nickname; // 닉네임

	@Column(name = "profile_path", length = 5000)
	private String profilePath; // 프로필 경로

	@Column(name = "is_alarmed")
	private Boolean isAlarmed; // 알림 유무

	@Column(name = "cell_phone")
	private String cellPhone;

	@Enumerated(EnumType.STRING)
	private Role role; // 역할

	@Column(name = "join_type")
	@Enumerated(EnumType.STRING)
	private JoinType joinType; // 가입 종류

	@Column(name = "user_state")
	@Enumerated(EnumType.STRING)
	private UserState userState; // 유저 상태

	@Column(name = "user_grade")
	@Enumerated(EnumType.STRING)
	private UserGrade userGrade;

	@Enumerated(EnumType.STRING)
	private Gender gender; // 성

	@Column(name = "birth_date")
	private LocalDateTime birthDate; // 생년월일

	@Column(name = "social_id")
	private String socialId; // 소셜 아이디

	@Column(name = "refresh_token")
	private String refreshToken; // refresh_token

	@Builder
	private User(
		String email, String nickname, String profilePath, Boolean isAlarmed, String cellPhone,
		Role role, JoinType joinType, UserState userState, UserGrade userGrade, Gender gender, LocalDateTime birthDate,
		String socialId, String refreshToken
	) {
		this.email = email;
		this.nickname = nickname;
		this.profilePath = profilePath;
		this.isAlarmed = isAlarmed;
		this.cellPhone = cellPhone;
		this.role = role;
		this.joinType = joinType;
		this.userState = userState;
		this.userGrade = userGrade;
		this.gender = gender;
		this.birthDate = birthDate;
		this.socialId = socialId;
		this.refreshToken = refreshToken;
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void deleteRefreshToken() {
		this.refreshToken = null;
	}

	public void rejoin() {
		this.deletedAt = null;
		this.userState = UserState.ACTIVE;
		this.userGrade = UserGrade.BASIC;
	}

	public void withdrawal() {
		this.deleteRefreshToken();
		this.userState = UserState.DELETED;
		this.deletedAt = LocalDateTime.now();
	}

	public String updateProfilePathAsDefault() {
		this.profilePath = ProfileImage.defaultImageUrl;
		return this.profilePath;
	}

	public void updateProfilePath(String uploadFileUrl) {
		this.profilePath = uploadFileUrl;
	}
}
