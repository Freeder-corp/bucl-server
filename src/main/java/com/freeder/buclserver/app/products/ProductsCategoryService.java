package com.freeder.buclserver.app.products;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.productcategory.dto.ProductCategoryDTO;
import com.freeder.buclserver.domain.productcategory.repository.ProductCategoryRepository;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

@Service
public class ProductsCategoryService {
	private final ProductCategoryRepository productCategoryRepository;
	private final ImageParsing imageParsing;

	public ProductsCategoryService(ProductCategoryRepository productCategoryRepository, ImageParsing imageParsing) {
		this.productCategoryRepository = productCategoryRepository;
		this.imageParsing = imageParsing;
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryDTO> getCategoryProducts(Long categoryId, int page, int pageSize) {
		try {
			Pageable pageable = PageRequest.of(page - 1, pageSize);
			Page<Product> categoryProductsPage = productCategoryRepository.findProductsByCategory(categoryId, pageable);
			List<ProductCategoryDTO> categoryProducts = categoryProductsPage.getContent().stream()
				.map(this::convertToCategoryDTO)
				.collect(Collectors.toList());
			return categoryProducts;
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "카테고리 제품 조회 - 서버 에러");
		}
	}

	public ProductCategoryDTO convertToCategoryDTO(Product product) {
		List<ProductReview> reviews = product.getReviews();
		int reviewCount = reviews.size();
		float averageRating = calculateAverageRating(reviews);
		String thumbnailUrl = imageParsing.getThumbnailUrl(product.getImagePath());
		averageRating = Math.round(averageRating * 10.0f) / 10.0f;

		return new ProductCategoryDTO(
			product.getId(),
			product.getName(),
			thumbnailUrl,
			product.getSalePrice(),
			product.getConsumerPrice(),
			product.getConsumerPrice() * product.getConsumerRewardRate(),
			product.getDiscountRate(),
			reviewCount,
			averageRating
		);
	}

	public float calculateAverageRating(List<ProductReview> reviews) {
		try {
			if (reviews.isEmpty()) {
				return 0.0f;
			}

			float totalRating = 0.0f;
			for (ProductReview review : reviews) {
				totalRating += review.getStarRate().getValue();
			}

			float averageRating = totalRating / reviews.size();

			return Math.round(averageRating * 10.0f) / 10.0f;
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "별점 평균 계산 - 서버 에러");
		}
	}
}
