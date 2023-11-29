package com.freeder.buclserver.app.rewards;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.core.config.security.CustomUserDetails;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.vo.RewardType;
import com.freeder.buclserver.global.response.BaseResponse;
import com.freeder.buclserver.global.response.ErrorResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/rewards")
@Tag(name = "rewards API", description = "적립금 관련 API")
public class RewardsController {

	private final RewardsService rewardsService;

	@Autowired
	public RewardsController(RewardsService rewardsService) {
		this.rewardsService = rewardsService;
	}

	@GetMapping
	public BaseResponse<?> getUserRewards(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "type", required = false) RewardType type
	) {
		if (userDetails == null) {
			return new BaseResponse<>(new ErrorResponse(HttpStatus.UNAUTHORIZED, "사용자 정보 조회 에러"),
				HttpStatus.UNAUTHORIZED, "사용자 정보 조회 에러");
		}

		Long userId = getUserIdFromUserDetails(userDetails);

		try {
			List<Reward> userRewards;
			if (type == null) {
				// 타입이 지정되지 않은 경우 모든 리워드 조회
				userRewards = rewardsService.getUserRewards(userId);
			} else {
				// 타입이 지정된 경우 해당 타입의 리워드 조회
				userRewards = rewardsService.getUserRewardsByType(userId, type);
			}

			return new BaseResponse<>(userRewards, HttpStatus.OK, "적립금 조회 성공");
		} catch (Exception e) {
			return new BaseResponse<>(
				new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "적립금 조회 에러"),
				HttpStatus.INTERNAL_SERVER_ERROR, "적립금 조회 에러");
		}
	}

	private Long getUserIdFromUserDetails(CustomUserDetails userDetails) {
		return Long.parseLong(userDetails.getUserId());
	}
}