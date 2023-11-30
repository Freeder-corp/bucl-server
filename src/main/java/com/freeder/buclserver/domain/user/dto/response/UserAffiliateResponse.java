package com.freeder.buclserver.domain.user.dto.response;

import com.freeder.buclserver.domain.affiliate.entity.Affiliate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAffiliateResponse {

	private Long affiliateId;
	private String imagePath;
	private String detailImagePath;
	private String brandName;
	private String productName;
	private int receivedRewardAmount;
	private String affiliateUrl;

	public static UserAffiliateResponse from(Affiliate affiliate, int totalReceivedReward) {
		return new UserAffiliateResponse(
			affiliate.getId(),
			affiliate.getProduct().getImagePath(),
			affiliate.getProduct().getDetailImagePath(),
			affiliate.getProduct().getBrandName(),
			affiliate.getProduct().getName(),
			totalReceivedReward,
			affiliate.getAffiliateUrl()
		);
	}
}
