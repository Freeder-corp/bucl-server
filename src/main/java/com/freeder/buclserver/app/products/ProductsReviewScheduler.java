package com.freeder.buclserver.app.products;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.freeder.buclserver.global.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductsReviewScheduler {
	private final ProductsReviewService productsReviewService;

	public ProductsReviewScheduler(ProductsReviewService productsReviewService) {
		this.productsReviewService = productsReviewService;
	}

	@Scheduled(cron = "0 0 4 * * ?")
	public void cleanupOldReviews() {
		try {
			LocalDateTime threeMonthsAgo = LocalDateTime.now().minus(3, ChronoUnit.MONTHS);
			productsReviewService.cleanupOldReviews(threeMonthsAgo);
		} catch (Exception e) {
			log.error("리뷰 데이터 삭제 중 오류 발생", e);
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "리뷰 데이터 삭제 중 오류 발생");
		}
	}
}
