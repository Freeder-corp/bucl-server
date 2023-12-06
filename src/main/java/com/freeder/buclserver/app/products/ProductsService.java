package com.freeder.buclserver.app.products;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

@Service
public class ProductsService {

	private final ProductsCategoryService productsCategoryService;
	private final ProductRepository productRepository;
	private final ProductOptionRepository productOptionRepository;
	private final ImageParsing imageParsing;

	@Autowired
	public ProductsService(
		ProductsCategoryService productsCategoryService,
		ProductRepository productRepository,
		ProductOptionRepository productOptionRepository,
		ImageParsing imageParsing
	) {
		this.productsCategoryService = productsCategoryService;
		this.productRepository = productRepository;
		this.productOptionRepository = productOptionRepository;
		this.imageParsing = imageParsing;
	}

	@Transactional(readOnly = true)
	public List<ProductDTO> getProducts(Long categoryId, int page, int pageSize) {
		try {
			Pageable pageable = PageRequest.of(page, pageSize);
			Page<Product> productsPage = productRepository.findProductsByConditions(categoryId, pageable);
			List<ProductDTO> products = productsPage.getContent().stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
			return products;
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 목록 조회 - 서버 에러");
		}
	}

	@Transactional(readOnly = true)
	public ProductDetailDTO getProductDetail(Long productCode) {
		try {
			Product product = productRepository.findAvailableProductByCode(productCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Product not found with code: " + productCode));

			List<ProductReview> reviews = product.getReviews().stream()
				.limit(3)
				.collect(Collectors.toList());

			float averageRating = productsCategoryService.calculateAverageRating(reviews);
			int reviewCount = reviews.size();

			List<ReviewPreviewDTO> reviewPreviews = reviews.stream()
				.map(this::convertToReviewPreviewDTO)
				.collect(Collectors.toList());

			List<String> imageUrls = imageParsing.getImageList(product.getImagePath());
			List<String> firstFiveImages = imageUrls.stream().limit(5).collect(Collectors.toList());

			return new ProductDetailDTO(
				product.getProductCode(),
				product.getName(),
				product.getBrandName(),
				product.getSalePrice(),
				product.getConsumerPrice(),
				product.getDiscountRate(),
				averageRating,
				product.getCreatedAt(),
				reviewCount,
				firstFiveImages,
				reviewPreviews
			);
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 상세 정보 조회 - 서버 에러");
		}
	}

	public ReviewPreviewDTO convertToReviewPreviewDTO(ProductReview review) {
		try {
			String thumbnailUrl = imageParsing.getThumbnailUrl(review.getImagePath());
			return new ReviewPreviewDTO(
				review.getUser().getProfilePath(),
				review.getUser().getNickname(),
				review.getCreatedAt(),
				review.getProduct().getName(),
				review.getContent(),
				thumbnailUrl
			);
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 리뷰 미리보기 변환 - 서버 에러");
		}
	}

	@Transactional(readOnly = true)
	public List<ProductOptionDTO> getProductOptions(Long productCode) {
		try {
			List<ProductOption> productOptions = productOptionRepository.findByProductProductCodeWithConditions(
				productCode);
			return productOptions.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 옵션 조회 - 서버 에러");
		}
	}

	private ProductOptionDTO convertToDTO(ProductOption productOption) {
		String[] values = productOption.getOptionValue().split(",");
		return new ProductOptionDTO(Arrays.asList(values), productOption.getOptionExtraAmount());
	}

	private ProductDTO convertToDTO(Product product) {
		try {
			String thumbnailUrl = imageParsing.getThumbnailUrl(product.getImagePath());
			float calculatedReward = (product.getSalePrice() * product.getConsumerRewardRate()) / 100;
			float roundedReward = Math.round(calculatedReward * 100.0f) / 100.0f;

			return new ProductDTO(
				product.getProductCode(),
				product.getName(),
				product.getBrandName(),
				thumbnailUrl,
				product.getSalePrice(),
				product.getConsumerPrice(),
				roundedReward
			);
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 정보 변환 - 서버 에러");
		}
	}
}
