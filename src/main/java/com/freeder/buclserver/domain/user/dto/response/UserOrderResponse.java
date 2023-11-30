package com.freeder.buclserver.domain.user.dto.response;

import java.time.LocalDateTime;

import org.springframework.web.util.UriComponentsBuilder;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.vo.CsStatus;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserOrderResponse {

	private Long orderId;
	private Long productId;
	private LocalDateTime orderDate;
	private String imagePath;
	private String brandName;
	private String productName;
	private int productAmount;
	private int productOrderQty;
	private CsStatus csStatus;
	private ShippingStatus shippingStatus;
	private String deliveryTrackingUrl;

	public static UserOrderResponse from(ConsumerOrder consumerOrder, int productOrderQty, Shipping shipping) {
		return new UserOrderResponse(
			consumerOrder.getId(),
			consumerOrder.getProduct().getId(),
			consumerOrder.getCreatedAt(),
			consumerOrder.getProduct().getImagePath(),
			consumerOrder.getProduct().getBrandName(),
			consumerOrder.getProduct().getName(),
			consumerOrder.getTotalOrderAmount(),
			productOrderQty,
			consumerOrder.getCsStatus(),
			shipping.getShippingStatus(),
			createDeliveryTrackingUrl(shipping)
		);
	}

	private static String createDeliveryTrackingUrl(Shipping shipping) {
		return UriComponentsBuilder.newInstance()
			.scheme("https")
			.host("search.naver.com")
			.path("search.naver")
			.queryParam("where", "nexearch")
			.queryParam("sm", "hty")
			.queryParam("fbm", "0")
			.queryParam("ie", "utf8")
			.queryParam("query", shipping.getShippingInfo().getShippingCoName() + shipping.getTrackingNum())
			.toUriString();
	}
}