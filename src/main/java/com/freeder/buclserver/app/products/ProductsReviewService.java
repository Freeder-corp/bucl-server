package com.freeder.buclserver.app.products;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.productreview.dto.ReviewDTO;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.productreview.repository.ProductReviewRepository;
import com.freeder.buclserver.global.util.ImageParsing;

@Service
public class ProductsReviewService {
	@Autowired
	private ProductReviewRepository productReviewRepository;
	@Autowired
	private ImageParsing imageParsing;
	@Autowired
	private ProductsCategoryService productsCategoryService;

	public ProductReviewResult getProductReviews(Long productId, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		Pageable pageable = PageRequest.of(offset, pageSize);
		Page<ProductReview> reviewPage = productReviewRepository.findByProductId(productId, pageable);

		long reviewCount = productReviewRepository.countByProductIdFk(productId);
		double averageRating = productsCategoryService.calculateAverageRating(reviewPage.getContent());

		List<ReviewDTO> reviewDTOs = reviewPage.getContent().stream()
			.map(this::convertToReviewDTO)
			.collect(Collectors.toList());

		return new ProductReviewResult(reviewCount, averageRating, reviewDTOs);
	}

	private ReviewDTO convertToReviewDTO(ProductReview review) {
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
	}

	public static class ProductReviewResult {
		private final long reviewCount;
		private final double averageRating;
		private final List<ReviewDTO> reviews;

		public ProductReviewResult(long reviewCount, double averageRating, List<ReviewDTO> reviews) {
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

		public double getAverageRating() {
			return averageRating;
		}
	}
}
