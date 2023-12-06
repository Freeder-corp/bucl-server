package com.freeder.buclserver.app.products;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.domain.productreview.dto.ReviewPhotoDTO;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.productreview.repository.ProductReviewRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductsReviewPhotoService {

	private final ProductReviewRepository productReviewRepository;
	private final ImageParsing imageParsing;

	public ProductsReviewPhotoService(ProductReviewRepository productReviewRepository, ImageParsing imageParsing) {
		this.productReviewRepository = productReviewRepository;
		this.imageParsing = imageParsing;
	}

	@Transactional(readOnly = true)
	public List<ReviewPhotoDTO> getProductReviewPhotos(Long productCode, int page, int pageSize) {
		try {
			Pageable pageable = PageRequest.of(page, pageSize);
			Page<ProductReview> reviewPage = productReviewRepository.findByProductProductCodeWithConditions(productCode,
				pageable);

			List<ReviewPhotoDTO> reviewPhotos = reviewPage.getContent().stream()
				.map(this::convertToPhotoDTO)
				.collect(Collectors.toList());

			log.info("상품 리뷰 사진 조회 성공 - productCode: {}, page: {}, pageSize: {}", productCode, page, pageSize);
			return reviewPhotos;
		} catch (Exception e) {
			log.error("상품 리뷰 사진 조회 실패 - productCode: {}, page: {}, pageSize: {}", productCode, page, pageSize, e);
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 리뷰 사진 조회 - 서버 에러");
		}
	}

	private ReviewPhotoDTO convertToPhotoDTO(ProductReview review) {
		try {
			String thumbnailUrl = imageParsing.getThumbnailUrl(review.getImagePath());
			log.info("상품 리뷰 사진 변환 성공 - reviewId: {}", review.getId());
			return new ReviewPhotoDTO(thumbnailUrl);
		} catch (Exception e) {
			log.error("상품 리뷰 사진 변환 실패 - reviewId: {}", review.getId(), e);
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 리뷰 사진 변환 - 서버 에러");
		}
	}
}