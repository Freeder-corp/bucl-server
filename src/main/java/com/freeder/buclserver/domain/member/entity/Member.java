package com.freeder.buclserver.domain.member.entity;

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
import com.freeder.buclserver.domain.member.vo.Gender;
import com.freeder.buclserver.domain.member.vo.JoinType;
import com.freeder.buclserver.domain.member.vo.MemberGrade;
import com.freeder.buclserver.domain.member.vo.MemberState;
import com.freeder.buclserver.domain.member.vo.Role;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.rewardwithdrawal.entity.RewardWithdrawal;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.wish.entity.Wish;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "member")
public class Member extends TimestampMixin {
	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "member")
	private List<Wish> wishes = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<Reward> rewards = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<ProductReview> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<Affiliate> affiliates = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<ShippingAddress> shippingAddresses = new ArrayList<>();

	@OneToMany(mappedBy = "consumer")
	private List<ConsumerOrder> consumerOrders = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<RewardWithdrawal> rewardWithdrawals = new ArrayList<>();

	@Column(length = 320)
	private String email; // 이메일
	private String nickname; // 닉네임

	@Column(name = "hashed_pw")
	private String hashedPw; // 비밀번호

	@Column(name = "profile_path")
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

	@Column(name = "member_state")
	@Enumerated(EnumType.STRING)
	private MemberState memberState; // 유저 상태

	@Column(name = "member_grade")
	@Enumerated(EnumType.STRING)
	private MemberGrade memberGrade;

	@Enumerated(EnumType.STRING)
	private Gender gender; // 성

	@Column(name = "birth_date")
	private LocalDateTime birthDate; // 생년월일

	@Column(name = "social_id")
	private String socialId; // 소셜 아이디

	@Column(name = "refresh_token")
	private String refreshToken; // refresh_token
}