package com.freeder.buclserver.app.products;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.productreview.dto.ReviewDTO;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.productreview.repository.ProductReviewRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

@Service
public class ProductsReviewService {

	private final ProductReviewRepository productReviewRepository;

	private final ImageParsing imageParsing;

	private final ProductsCategoryService productsCategoryService;

	public ProductsReviewService(ProductReviewRepository productReviewRepository, ImageParsing imageParsing,
		ProductsCategoryService productsCategoryService) {
		this.productReviewRepository = productReviewRepository;
		this.imageParsing = imageParsing;
		this.productsCategoryService = productsCategoryService;
	}

	public ProductReviewResult getProductReviews(Long productCode, int page, int pageSize) {
		try {
			int offset = (page - 1) * pageSize;
			Pageable pageable = PageRequest.of(offset, pageSize);
			Page<ProductReview> reviewPage = productReviewRepository.findByProductProductCodeWithConditions(
				productCode, pageable);

			long reviewCount = productReviewRepository.countByProductCodeFkWithConditions(productCode);
			float averageRating = productsCategoryService.calculateAverageRating(reviewPage.getContent());

			List<ReviewDTO> reviewDTOs = reviewPage.getContent().stream()
				.map(this::convertToReviewDTO)
				.sorted(Comparator
					.comparingDouble(ReviewDTO::getStarRate)
					.thenComparing(ReviewDTO::getCreatedAt).reversed())
				.collect(Collectors.toList());

			return new ProductReviewResult(reviewCount, averageRating, reviewDTOs);
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 리뷰 조회 - 서버 에러");
		}
	}

	private ReviewDTO convertToReviewDTO(ProductReview review) {
		try {
			List<String> reviewUrls = imageParsing.getReviewUrl(review.getImagePath());
			return new ReviewDTO(
				review.getUser().getProfilePath(),
				review.getUser().getNickname(),
				review.getCreatedAt(),
				review.getStarRate().getValue(),
				review.getSelectedOption(),
				reviewUrls,
				review.getContent()
			);
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 리뷰 변환 - 서버 에러");
		}
	}

	public static class ProductReviewResult {
		private final long reviewCount;
		private final float averageRating;
		private final List<ReviewDTO> reviews;

		public ProductReviewResult(long reviewCount, float averageRating, List<ReviewDTO> reviews) {
			this.reviewCount = reviewCount;
			this.averageRating = averageRating;
			this.reviews = reviews;
		}

		public List<ReviewDTO> getReviews() {
			return reviews;
		}

		public long getReviewCount() {
			return reviewCount;
		}

		public float getAverageRating() {
			return averageRating;
		}
	}
}
