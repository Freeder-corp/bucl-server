package com.freeder.buclserver.app.products;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProductsReviewScheduler {
	private final ProductsReviewService productsReviewService;

	public ProductsReviewScheduler(ProductsReviewService productsReviewService) {
		this.productsReviewService = productsReviewService;
	}

	@Scheduled(cron = "0 0 4 * * ?")
	public void cleanupOldReviews() {
		try {
			LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
			productsReviewService.cleanupOldReviews(threeMonthsAgo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
