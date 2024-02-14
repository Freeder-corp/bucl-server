package com.freeder.buclserver.products;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.freeder.buclserver.app.products.ProductsReviewScheduler;

@SpringBootTest
@DisplayName("3개월 이상된 리뷰 삭제 API")
public class ReviewCleanupSchedulerTest {

	@Autowired
	private ProductsReviewScheduler productsReviewScheduler;

	@Test
	public void testCleanupScheduler() {
		productsReviewScheduler.cleanupOldReviews();
	}
}
