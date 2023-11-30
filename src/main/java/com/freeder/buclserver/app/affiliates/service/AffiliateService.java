package com.freeder.buclserver.app.affiliates.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.affiliate.entity.Affiliate;
import com.freeder.buclserver.domain.affiliate.repository.AffiliateRepository;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.user.dto.response.UserAffiliateResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AffiliateService {

	private final AffiliateRepository affiliateRepository;
	private final RewardRepository rewardRepository;

	@Transactional(readOnly = true)
	public List<UserAffiliateResponse> getMyAffiliates(Long userId) {
		List<UserAffiliateResponse> affiliateResponseList = new ArrayList<>();

		List<Affiliate> affiliateList = affiliateRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);

		for (Affiliate affiliate : affiliateList) {
			int totalReceivedReward = rewardRepository.findReceivedRewardAmount(
				affiliate.getUser().getId(),
				affiliate.getProduct().getId()
			);
			UserAffiliateResponse affiliateResponse = UserAffiliateResponse.from(affiliate, totalReceivedReward);
			affiliateResponseList.add(affiliateResponse);
		}

		return affiliateResponseList;
	}
}
