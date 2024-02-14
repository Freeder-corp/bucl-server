package com.freeder.buclserver.products;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.freeder.buclserver.app.products.ProductsReviewPhotoService;
import com.freeder.buclserver.domain.productreview.dto.ReviewPhotoDTO;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.productreview.repository.ProductReviewRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

@DisplayName("상품 리뷰 사진 조회 API")
public class ProductsReviewPhotoServiceTest {

	@Mock
	private ProductReviewRepository productReviewRepository;

	@Mock
	private ImageParsing imageParsing;

	@InjectMocks
	private ProductsReviewPhotoService productsReviewPhotoService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@DisplayName("리뷰 사진 조회 성공 테스트")
	public void 리뷰사진조회() {
		// Given
		Long productCode = 123L;
		int page = 0;
		int pageSize = 10;

		ProductReview productReview1 = new ProductReview();
		productReview1.setId(1L);
		productReview1.setImagePath("image1.jpg");

		ProductReview productReview2 = new ProductReview();
		productReview2.setId(2L);
		productReview2.setImagePath("image2.jpg");

		Page<ProductReview> pageMock = mock(Page.class);
		when(pageMock.getContent()).thenReturn(Arrays.asList(productReview1, productReview2));

		when(
			productReviewRepository.findByProductProductCodeWithConditions(productCode, PageRequest.of(page, pageSize)))
			.thenReturn(Optional.of(pageMock));

		when(imageParsing.getImageList("image1.jpg")).thenReturn(Arrays.asList("image1.jpg"));
		when(imageParsing.getImageList("image2.jpg")).thenReturn(Arrays.asList("image2.jpg"));

		// When
		List<ReviewPhotoDTO> result = productsReviewPhotoService.getProductReviewPhotos(productCode, page, pageSize);

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("image1.jpg", result.get(0).getImagePath());
		assertEquals("image2.jpg", result.get(1).getImagePath());
	}

	@Test
	@DisplayName("리뷰 사진 조회 테스트 - 예외(리뷰 없음)")
	public void 리뷰사진조회_리뷰없음() {
		// Given
		Long productCode = 123L;
		int page = 0;
		int pageSize = 10;

		when(
			productReviewRepository.findByProductProductCodeWithConditions(productCode, PageRequest.of(page, pageSize)))
			.thenReturn(Optional.empty());

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewPhotoService.getProductReviewPhotos(productCode, page, pageSize),
			"해당 리뷰사진을 찾을 수 없음"
		);
	}

	@Test
	@DisplayName("리뷰 사진 조회 테스트 - 예외(데이터베이스 조회 실패)")
	public void 리뷰사진조회_데이터베이스에러() {
		// Given
		Long productCode = 123L;
		int page = 0;
		int pageSize = 10;

		when(
			productReviewRepository.findByProductProductCodeWithConditions(productCode, PageRequest.of(page, pageSize)))
			.thenThrow(new RuntimeException("Database failure"));

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewPhotoService.getProductReviewPhotos(productCode, page, pageSize),
			"리뷰 사진 조회 - 데이터베이스 에러"
		);
	}

	@Test
	@DisplayName("리뷰 사진 조회 테스트 - 예외(이미지 변환)")
	public void 리뷰사진조회_이미지변환() {
		// Given
		Long productCode = 123L;
		int page = 0;
		int pageSize = 10;

		ProductReview productReview1 = new ProductReview();
		productReview1.setId(1L);
		productReview1.setImagePath("image1.jpg");

		Page<ProductReview> pageMock = mock(Page.class);
		when(pageMock.getContent()).thenReturn(Arrays.asList(productReview1));

		when(
			productReviewRepository.findByProductProductCodeWithConditions(productCode, PageRequest.of(page, pageSize)))
			.thenReturn(Optional.of(pageMock));

		when(imageParsing.getImageList("image1.jpg")).thenThrow(new RuntimeException("이미지변환 에러입니다."));

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewPhotoService.getProductReviewPhotos(productCode, page, pageSize),
			"상품 리뷰 사진 DTO 변환 - 서버 에러"
		);
	}

}
