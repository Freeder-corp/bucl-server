package com.freeder.buclserver.app.products;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.product.dto.ProductDTO;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.productcategory.dto.ProductCategoryDTO;
import com.freeder.buclserver.domain.productcategory.repository.ProductCategoryRepository;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;

@Service
public class ProductsService {

	private final ProductRepository productRepository;
	private final ProductCategoryRepository productCategoryRepository;

	public ProductsService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
		this.productRepository = productRepository;
		this.productCategoryRepository = productCategoryRepository;
	}

	public List<ProductDTO> getProducts(Long categoryId, int page, int pageSize) {
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<Product> productsPage = productRepository.findProductsOrderByReward(categoryId, pageable);
		List<ProductDTO> products = productsPage.getContent().stream()
			.map(this::convertToDTO)
			.collect(Collectors.toList());
		return products;
	}

	public List<ProductCategoryDTO> getCategoryProducts(Long categoryId, int page, int pageSize) {
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<Product> categoryProductsPage = productCategoryRepository.findProductsByCategory(categoryId, pageable);
		List<ProductCategoryDTO> categoryProducts = categoryProductsPage.getContent().stream()
			.map(this::convertToCategoryDTO)
			.collect(Collectors.toList());
		return categoryProducts;
	}

	private ProductDTO convertToDTO(Product product) {
		return new ProductDTO(
			product.getId(),
			product.getName(),
			product.getBrandName(),
			product.getImagePath(),
			product.getSalePrice(),
			product.getConsumerPrice(),
			product.getConsumerPrice() * product.getConsumerRewardRate()
		);
	}

	public ProductCategoryDTO convertToCategoryDTO(Product product) {
		List<ProductReview> reviews = product.getReviews();
		int reviewCount = reviews.size();
		double averageRating = calculateAverageRating(reviews);

		return new ProductCategoryDTO(
			product.getId(),
			product.getName(),
			product.getImagePath(),
			product.getSalePrice(),
			product.getConsumerPrice(),
			product.getConsumerPrice() * product.getConsumerRewardRate(),
			product.getDiscountRate(),
			reviewCount,
			averageRating
		);
	}

	private double calculateAverageRating(List<ProductReview> reviews) {
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
