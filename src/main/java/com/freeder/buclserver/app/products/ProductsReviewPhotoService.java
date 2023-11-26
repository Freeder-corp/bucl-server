package com.freeder.buclserver.app.products;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.productreview.dto.ReviewPhotoDTO;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.productreview.repository.ProductReviewRepository;
import com.freeder.buclserver.global.util.ImageParsing;

@Service
public class ProductsReviewPhotoService {

	@Autowired
	private ProductReviewRepository productReviewRepository;

	@Autowired
	private ImageParsing imageParsing;

	public List<ReviewPhotoDTO> getProductReviewPhotos(Long productId, int page, int pageSize) {
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<ProductReview> reviewPage = productReviewRepository.findByProductId(productId, pageable);

		List<ReviewPhotoDTO> reviewPhotos = reviewPage.getContent().stream()
			.map(this::convertToPhotoDTO)
			.collect(Collectors.toList());

		return reviewPhotos;
	}

	private ReviewPhotoDTO convertToPhotoDTO(ProductReview review) {
		String thumbnailUrl = imageParsing.getThumbnailUrl(review.getImagePath());
		return new ReviewPhotoDTO(thumbnailUrl);
	}
}