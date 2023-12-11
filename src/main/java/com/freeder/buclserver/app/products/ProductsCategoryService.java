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
import com.freeder.buclserver.domain.wish.repository.WishRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductsCategoryService {
	private final ProductCategoryRepository productCategoryRepository;
	private final WishRepository wishRepository;
	private final ImageParsing imageParsing;

	public ProductsCategoryService(ProductCategoryRepository productCategoryRepository, WishRepository wishRepository,
		ImageParsing imageParsing) {
		this.productCategoryRepository = productCategoryRepository;
		this.wishRepository = wishRepository;
		this.imageParsing = imageParsing;
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryDTO> getCategoryProducts(Long categoryId, int page, int pageSize, Long userId) {
		try {
			Pageable pageable = PageRequest.of(page, pageSize);
			Page<Product> categoryProductsPage = productCategoryRepository.findProductsByCategory(categoryId, pageable);
			List<ProductCategoryDTO> categoryProducts = categoryProductsPage.getContent().stream()
				.map(product -> convertToCategoryDTO(product, userId))
				.collect(Collectors.toList());

			log.info("카테고리 제품 조회 성공 - categoryId: {}, page: {}, pageSize: {}", categoryId, page, pageSize);
			return categoryProducts;
		} catch (Exception e) {
			log.error("카테고리 제품 조회 실패 - categoryId: {}, page: {}, pageSize: {}", categoryId, page, pageSize, e);
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "카테고리 제품 조회 - 서버 에러");
		}
	}

	public ProductCategoryDTO convertToCategoryDTO(Product product, Long userId) {
		List<ProductReview> reviews = product.getReviews();
		int reviewCount = reviews.size();
		float averageRating = calculateAverageRating(reviews);
		String thumbnailUrl = imageParsing.getThumbnailUrl(product.getImagePath());
		averageRating = Math.round(averageRating * 10.0f) / 10.0f;

		boolean wished = false;

		if (userId != null) {
			wished = wishRepository.existsByUser_IdAndProduct_IdAndDeletedAtIsNull(userId, product.getId());
		}
		return new ProductCategoryDTO(
			product.getProductCode(),
			product.getName(),
			thumbnailUrl,
			product.getSalePrice(),
			product.getConsumerPrice(),
			Math.round(product.getConsumerPrice() * product.getConsumerRewardRate()),
			product.getDiscountRate(),
			reviewCount,
			averageRating,
			wished
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
			log.error("별점 평균 계산 실패", e);
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "별점 평균 계산 - 서버 에러");
		}
	}
}
