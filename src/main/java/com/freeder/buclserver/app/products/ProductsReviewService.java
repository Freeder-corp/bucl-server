package com.freeder.buclserver.app.products;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.productreview.dto.ReviewDTO;
import com.freeder.buclserver.domain.productreview.dto.ReviewRequestDTO;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.productreview.repository.ProductReviewRepository;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductsReviewService {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	private final ProductReviewRepository productReviewRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	private final ImageParsing imageParsing;

	private final ProductsCategoryService productsCategoryService;

	public ProductsReviewService(ProductReviewRepository productReviewRepository,
		UserRepository userRepository,
		ProductRepository productRepository,
		ImageParsing imageParsing,
		ProductsCategoryService productsCategoryService) {
		this.productReviewRepository = productReviewRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
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

	public void createOrUpdateReview(Long productCode, ReviewRequestDTO reviewRequestDTO, Long userId,
		List<String> s3ImageUrls) {
		try {
			Optional<ProductReview> existingReviewOptional = productReviewRepository.findFirstByUserIdAndProductCode(
				userId,
				productCode
			);

			if (existingReviewOptional.isPresent()) {

				ProductReview existingReview = existingReviewOptional.get();
				existingReview.setContent(reviewRequestDTO.getReviewContent());
				existingReview.setStarRate(reviewRequestDTO.getStarRate());
				existingReview.setUpdatedAt(LocalDateTime.now());
				existingReview.setImagePath(existingReview.getImagePath() + " " + String.join(" ", s3ImageUrls));

				productReviewRepository.save(existingReview);

			} else {

				User user = userRepository.findById(userId)
					.orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, 404, "사용자를 찾을 수 없음"));

				Product product = productRepository.findAvailableProductByCode(productCode)
					.orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, 404, "상품을 찾을 수 없음"));

				ProductReview newReview = new ProductReview();
				newReview.setUser(user);
				newReview.setProduct(product);
				newReview.setContent(reviewRequestDTO.getReviewContent());
				newReview.setStarRate(reviewRequestDTO.getStarRate());
				newReview.setCreatedAt(LocalDateTime.now());
				newReview.setImagePath(String.join(" ", s3ImageUrls));
				newReview.setProductCode(productCode);

				productReviewRepository.save(newReview);

			}
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "리뷰 생성 또는 수정 - 서버 에러");
		}
	}

	public void deleteReview(Long productCode, Long reviewId, Long userId) {
		try {
			ProductReview reviewToDelete = productReviewRepository.findByIdAndProduct_ProductCodeAndUser_Id(reviewId,
					productCode, userId)
				.orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, 404, "해당 리뷰를 찾을 수 없음"));

			if (!reviewToDelete.getUser().getId().equals(userId)) {
				throw new BaseException(HttpStatus.FORBIDDEN, 403, "해당 리뷰를 삭제할 권한이 없음");
			}

			reviewToDelete.setDeletedAt(LocalDateTime.now());
			productReviewRepository.save(reviewToDelete);
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "리뷰 삭제 - 서버 에러");
		}
	}

	public List<String> uploadImagesToS3(List<MultipartFile> images) {
		List<String> s3ImageUrls = new ArrayList<>();

		for (MultipartFile image : images) {
			try {
				String s3ImageUrl = uploadImageToS3(image);
				s3ImageUrls.add(s3ImageUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return s3ImageUrls;
	}

	private String uploadImageToS3(MultipartFile image) throws IOException {
		String originalFilename = image.getOriginalFilename();

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(image.getSize());
		metadata.setContentType(image.getContentType());

		AmazonS3Client amazonS3Client = new AmazonS3Client();
		amazonS3Client.putObject(bucket, originalFilename, image.getInputStream(), metadata);

		return amazonS3Client.getUrl(bucket, originalFilename).toString();
	}
}
