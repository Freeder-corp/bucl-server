package com.freeder.buclserver.app.affiliates.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.affiliate.entity.Affiliate;
import com.freeder.buclserver.domain.affiliate.repository.AffiliateRepository;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.user.dto.response.MyAffiliateResponse;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.global.exception.user.UserIdNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AffiliateService {

	private final UserRepository userRepository;
	private final AffiliateRepository affiliateRepository;
	private final RewardRepository rewardRepository;

	@Transactional(readOnly = true)
	public List<MyAffiliateResponse> getMyAffiliates(Long userId) {
		List<MyAffiliateResponse> affiliateResponseList = new ArrayList<>();

		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserIdNotFoundException(userId));

		List<Affiliate> affiliateList = affiliateRepository.findAllByUserOrderByCreatedAtDesc(user);

		for (Affiliate affiliate : affiliateList) {
			int totalReceivedReward = rewardRepository.findReceivedRewardAmount(
				affiliate.getUser().getId(),
				affiliate.getProduct().getId()
			);
			MyAffiliateResponse affiliateResponse = MyAffiliateResponse.from(affiliate, totalReceivedReward);
			affiliateResponseList.add(affiliateResponse);
		}

		return affiliateResponseList;
	}
}
