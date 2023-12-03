package com.freeder.buclserver.domain.reward.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardDto {
	private String brandName;
	private String name;
	private int reward;
	private LocalDateTime createdAt;
}
