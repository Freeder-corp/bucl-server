package com.freeder.buclserver.domain.reward.dto;

import java.time.LocalDateTime;

import com.freeder.buclserver.domain.reward.vo.RewardType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardDto {
	private String brandName;
	private String name;
	private int reward;
	private RewardType rewardType;
	private LocalDateTime createdAt;
}
