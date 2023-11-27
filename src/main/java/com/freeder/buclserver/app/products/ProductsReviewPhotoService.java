package com.freeder.buclserver.app.products;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.productreview.dto.ReviewPhotoDTO;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.productreview.repository.ProductReviewRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

@Service
public class ProductsReviewPhotoService {

	private final ProductReviewRepository productReviewRepository;
	private final ImageParsing imageParsing;

	public ProductsReviewPhotoService(ProductReviewRepository productReviewRepository, ImageParsing imageParsing) {
		this.productReviewRepository = productReviewRepository;
		this.imageParsing = imageParsing;
	}

	public List<ReviewPhotoDTO> getProductReviewPhotos(Long productCode, int page, int pageSize) {
		try {
			Pageable pageable = PageRequest.of(page - 1, pageSize);
			Page<ProductReview> reviewPage = productReviewRepository.findByProduct_productCode(productCode, pageable);

			List<ReviewPhotoDTO> reviewPhotos = reviewPage.getContent().stream()
				.map(this::convertToPhotoDTO)
				.collect(Collectors.toList());

			return reviewPhotos;
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 리뷰 사진 조회 - 서버 에러");
		}
	}

	private ReviewPhotoDTO convertToPhotoDTO(ProductReview review) {
		try {
			String thumbnailUrl = imageParsing.getThumbnailUrl(review.getImagePath());
			return new ReviewPhotoDTO(thumbnailUrl);
		} catch (Exception e) {
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "상품 리뷰 사진 변환 - 서버 에러");
		}
	}
}