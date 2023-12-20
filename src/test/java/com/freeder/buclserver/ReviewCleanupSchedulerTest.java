package com.freeder.buclserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.freeder.buclserver.app.products.ProductsReviewScheduler;

@SpringBootTest
public class ReviewCleanupSchedulerTest {

	@Autowired
	private ProductsReviewScheduler productsReviewScheduler;

	@Test
	public void testCleanupScheduler() {
		productsReviewScheduler.cleanupOldReviews();
	}
}
