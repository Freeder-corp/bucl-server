package com.freeder.buclserver.app.products;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.productcategory.dto.ProductCategoryDTO;
import com.freeder.buclserver.domain.productcategory.repository.ProductCategoryRepository;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.global.logic.ImageParsing;

@Service
public class ProductsCategoryService {
	private final ProductRepository productRepository;
	private final ProductCategoryRepository productCategoryRepository;
	private final ImageParsing imageParsing;

	public ProductsCategoryService(ProductRepository productRepository,
		ProductCategoryRepository productCategoryRepository, ImageParsing imageParsing) {
		this.productRepository = productRepository;
		this.productCategoryRepository = productCategoryRepository;
		this.imageParsing = imageParsing;
	}

	public List<ProductCategoryDTO> getCategoryProducts(Long categoryId, int page, int pageSize) {
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<Product> categoryProductsPage = productCategoryRepository.findProductsByCategory(categoryId, pageable);
		List<ProductCategoryDTO> categoryProducts = categoryProductsPage.getContent().stream()
			.map(this::convertToCategoryDTO)
			.collect(Collectors.toList());
		return categoryProducts;
	}

	public ProductCategoryDTO convertToCategoryDTO(Product product) {
		List<ProductReview> reviews = product.getReviews();
		int reviewCount = reviews.size();
		double averageRating = calculateAverageRating(reviews);
		String thumbnailUrl = imageParsing.getThumbnailUrl(product.getImagePath());
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

	public double calculateAverageRating(List<ProductReview> reviews) {
		if (reviews.isEmpty()) {
			return 0.0;
		}

		double totalRating = 0.0;
		for (ProductReview review : reviews) {
			totalRating += review.getStarRate().getValue();
		}

		double averageRating = totalRating / reviews.size();

		return Math.round(averageRating * 10.0) / 10.0;
	}
}
