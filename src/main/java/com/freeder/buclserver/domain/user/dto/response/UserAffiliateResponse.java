package com.freeder.buclserver.domain.user.dto.response;

import com.freeder.buclserver.domain.affiliate.entity.Affiliate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserAffiliateResponse {

	private Long affiliateId;
	private String imagePath;
	private String detailImagePath;
	private String brandName;
	private String productName;
	@Setter(AccessLevel.PRIVATE)
	private Integer receivedRewardAmount;
	private String affiliateUrl;

	public static UserAffiliateResponse from(Affiliate affiliate) {
		return new UserAffiliateResponse(
			affiliate.getId(),
			affiliate.getProduct().getImagePath(),
			affiliate.getProduct().getDetailImagePath(),
			affiliate.getProduct().getBrandName(),
			affiliate.getProduct().getName(),
			null,
			affiliate.getAffiliateUrl()
		);
	}

	public void updateReceivedReward(int price) {
		this.setReceivedRewardAmount(price);
	}
}
