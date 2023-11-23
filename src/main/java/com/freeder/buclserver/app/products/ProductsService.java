package com.freeder.buclserver.app.products;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.freeder.buclserver.domain.product.dto.ProductDTO;
import com.freeder.buclserver.domain.product.dto.ProductDetailDTO;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.productoption.dto.ProductOptionDTO;
import com.freeder.buclserver.domain.productoption.entity.ProductOption;
import com.freeder.buclserver.domain.productoption.repository.ProductOptionRepository;
import com.freeder.buclserver.domain.productreview.dto.ReviewPreviewDTO;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.global.logic.ImageParsing;

@Service
public class ProductsService {

	@Autowired
	private final ProductsCategoryService productsCategoryService;
	private final ProductRepository productRepository;
	private final ProductOptionRepository productOptionRepository;
	private final ImageParsing imageParsing;

	public ProductsService(ProductRepository productRepository, ProductsCategoryService productsCategoryService,
		ProductOptionRepository productOptionRepository, ImageParsing imageParsing) {
		this.productRepository = productRepository;
		this.productsCategoryService = productsCategoryService;
		this.productOptionRepository = productOptionRepository;
		this.imageParsing = imageParsing;
	}

	public List<ProductDTO> getProducts(Long categoryId, int page, int pageSize) {
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<Product> productsPage = productRepository.findProductsOrderByReward(categoryId, pageable);
		List<ProductDTO> products = productsPage.getContent().stream()
			.map(this::convertToDTO)
			.collect(Collectors.toList());
		return products;
	}

	public ProductDetailDTO getProductDetail(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + productId));

		List<ProductReview> reviews = product.getReviews().stream()
			.limit(3)
			.collect(Collectors.toList());

		double averageRating = productsCategoryService.calculateAverageRating(reviews);

		List<ReviewPreviewDTO> reviewPreviews = reviews.stream()
			.map(this::convertToReviewPreviewDTO)
			.collect(Collectors.toList());
		List<String> imageUrls = imageParsing.getImageList(product.getImagePath());
		List<String> firstFiveImages = imageUrls.stream().limit(5).collect(Collectors.toList());
		return new ProductDetailDTO(
			product.getId(),
			product.getName(),
			product.getBrandName(),
			product.getSalePrice(),
			product.getConsumerPrice(),
			product.getDiscountRate(),
			averageRating,
			firstFiveImages,
			reviewPreviews
		);
	}

	public List<ProductOptionDTO> getProductOptions(Long productId) {
		List<ProductOption> productOptions = productOptionRepository.findByProductId(productId);
		return productOptions.stream()
			.map(this::convertToDTO)
			.collect(Collectors.toList());
	}

	private ProductOptionDTO convertToDTO(ProductOption productOption) {
		return new ProductOptionDTO(productOption.getOptionKey().name(), productOption.getOptionValue());
	}

	private ReviewPreviewDTO convertToReviewPreviewDTO(ProductReview review) {
		return new ReviewPreviewDTO(
			review.getUser().getProfilePath(),
			review.getUser().getNickname(),
			review.getCreatedAt(),
			review.getProduct().getName(),
			review.getContent()
		);
	}

	private ProductDTO convertToDTO(Product product) {
		String thumbnailUrl = imageParsing.getThumbnailUrl(product.getImagePath());
		return new ProductDTO(
			product.getId(),
			product.getName(),
			product.getBrandName(),
			thumbnailUrl,
			product.getSalePrice(),
			product.getConsumerPrice(),
			product.getConsumerPrice() * product.getConsumerRewardRate()
		);
	}

}
