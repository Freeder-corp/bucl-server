package com.freeder.buclserver.products;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.freeder.buclserver.app.products.ProductsReviewService;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.productreview.dto.ReviewRequestDTO;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.productreview.repository.ProductReviewRepository;
import com.freeder.buclserver.domain.productreview.vo.StarRate;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.user.repository.UserRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@DisplayName("상품 리뷰 생성/수정/삭제 API")
public class ProductsReviewServiceTest {
	@Mock
	private ProductReviewRepository productReviewRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ImageParsing imageParsing;

	@Mock
	private S3Client s3Client;

	@InjectMocks
	private ProductsReviewService productsReviewService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@DisplayName("리뷰가 존재할 때 수정 테스트 - 성공")
	public void 리뷰존재할때_수정테스트() {
		// Given
		Long productCode = 100000001L;
		Long userId = 1L;
		ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("Updated Content", StarRate.FOUR_AND_HALF);
		List<String> s3ImageUrls = Arrays.asList("url1", "url2");

		ProductReview existingReview = new ProductReview();
		existingReview.setId(1L);
		existingReview.setContent("Old Content");
		existingReview.setStarRate(StarRate.THREE);
		existingReview.setUpdatedAt(LocalDateTime.now());
		existingReview.setImagePath("oldImageUrl1 oldImageUrl2");

		List<String> prevS3Urls = Arrays.asList("oldImageUrl1", "oldImageUrl2");

		Optional<ProductReview> existingReviewOptional = Optional.of(existingReview);

		// Mocking
		when(productReviewRepository.save(any(ProductReview.class))).thenReturn(existingReview);
		when(productReviewRepository.findFirstByUserIdAndProductCode(userId, productCode)).thenReturn(
			existingReviewOptional);
		when(imageParsing.getImageList("oldImageUrl1 oldImageUrl2")).thenReturn(prevS3Urls);
		when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(null);

		// When
		productsReviewService.createOrUpdateReview(productCode, reviewRequestDTO, userId, s3ImageUrls);

		// Then
		verify(productReviewRepository).save(any(ProductReview.class));
		verify(s3Client, times(prevS3Urls.size())).deleteObject(any(DeleteObjectRequest.class));

		assertEquals("Updated Content", existingReview.getContent());
		assertEquals(StarRate.FOUR_AND_HALF, existingReview.getStarRate());
	}

	@Test
	@DisplayName("리뷰가 존재하지 않을 때 생성 테스트 - 성공")
	public void 리뷰존재하지않을때_생성테스트() {
		// Given
		Long productCode = 100000001L;
		Long userId = 1L;
		ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("New Content", StarRate.FOUR);
		List<String> s3ImageUrls = Arrays.asList("newUrl1", "newUrl2");

		// Mocking
		when(productReviewRepository.findFirstByUserIdAndProductCode(userId, productCode)).thenReturn(Optional.empty());

		User user = new User();
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		Product product = new Product();
		when(productRepository.findAvailableProductByCode(productCode)).thenReturn(Optional.of(product));

		// When
		productsReviewService.createOrUpdateReview(productCode, reviewRequestDTO, userId, s3ImageUrls);

		// Then
		verify(productReviewRepository).save(any(ProductReview.class));
	}

	@Test
	@DisplayName("리뷰가 존재할 때 삭제 테스트 - 성공")
	public void 리뷰가존재할때_삭제테스트() {
		// Given
		Long productCode = 100000002L;
		Long userId = 1L;
		Long reviewId = 22L;

		ProductReview existingReview = new ProductReview();
		existingReview.setId(reviewId);

		User user = new User();
		user.setId(userId);

		existingReview.setUser(user);
		existingReview.setContent("Old Content");
		existingReview.setStarRate(StarRate.THREE);
		existingReview.setUpdatedAt(LocalDateTime.now());
		existingReview.setImagePath("oldImageUrl1 oldImageUrl2");

		List<String> prevS3Urls = Arrays.asList("oldImageUrl1", "oldImageUrl2");

		Optional<ProductReview> existingReviewOptional = Optional.of(existingReview);

		// Mocking
		when(productReviewRepository.findByIdAndProduct_ProductCodeAndUser_Id(reviewId, productCode, userId))
			.thenReturn(existingReviewOptional);
		when(imageParsing.getImageList("oldImageUrl1 oldImageUrl2")).thenReturn(prevS3Urls);
		when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(null);

		// When
		List<String> deletedS3Urls = productsReviewService.deleteReview(productCode, reviewId, userId);

		// Then
		verify(productReviewRepository).findByIdAndProduct_ProductCodeAndUser_Id(reviewId, productCode, userId);
		verify(productReviewRepository).save(existingReview);

		assertEquals("Old Content", existingReview.getContent());
		assertEquals(StarRate.THREE, existingReview.getStarRate());
		assertEquals(prevS3Urls, deletedS3Urls);
	}

	@Test
	@DisplayName("유효하지 않은 상품 코드로 리뷰 생성 테스트 - 예외")
	public void 유효하지않은상품코드_리뷰생성테스트() {
		// Given
		Long invalidProductCode = 999999L;
		Long userId = 1L;
		ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("Invalid Product Code", StarRate.FIVE);
		List<String> s3ImageUrls = Arrays.asList("invalidUrl1", "invalidUrl2");

		// Mocking
		when(productRepository.findAvailableProductByCode(invalidProductCode)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewService.createOrUpdateReview(invalidProductCode, reviewRequestDTO, userId, s3ImageUrls),
			"유효하지 않은 상품 코드로 리뷰를 생성하려고 할 때 예외가 발생해야 합니다."
		);
	}

	@Test
	@DisplayName("유효하지 않은 사용자 ID로 리뷰 생성 테스트 - 예외")
	public void 유효하지않은사용자ID_리뷰생성테스트() {
		// Given
		Long productCode = 100000001L;
		Long invalidUserId = 999999L;
		ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("Invalid User ID", StarRate.TWO);
		List<String> s3ImageUrls = Arrays.asList("invalidUrl1", "invalidUrl2");

		// Mocking
		when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewService.createOrUpdateReview(productCode, reviewRequestDTO, invalidUserId, s3ImageUrls),
			"유효하지 않은 사용자 ID로 리뷰를 생성하려고 할 때 예외가 발생해야 합니다."
		);
	}

	@Test
	@DisplayName("리뷰 내용이 null일 때 리뷰 생성 테스트 - 예외")
	public void 리뷰내용이null일때_리뷰생성테스트() {
		// Given
		Long productCode = 100000001L;
		Long userId = 1L;
		ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("", StarRate.THREE);
		List<String> s3ImageUrls = Arrays.asList("url1", "url2");

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewService.createOrUpdateReview(productCode, reviewRequestDTO, userId, s3ImageUrls),
			"빈 리뷰 내용으로 리뷰를 생성하려고 할 때 예외가 발생해야 합니다."
		);
	}

	@Test
	@DisplayName("리뷰 별점이 null일 때 리뷰 생성 테스트 - 예외")
	public void 리뷰별점이null일때_리뷰생성테스트() {
		// Given
		Long productCode = 100000001L;
		Long userId = 1L;
		ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("Null Star Rate", null);  // 리뷰 별점이 null
		List<String> s3ImageUrls = Arrays.asList("url1", "url2");

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewService.createOrUpdateReview(productCode, reviewRequestDTO, userId, s3ImageUrls),
			"리뷰 별점이 null일 때 리뷰를 생성하려고 할 때 예외가 발생해야 합니다."
		);
	}

	@Test
	@DisplayName("리뷰가 존재하지 않을 때 삭제 실패 테스트 - 예외")
	public void 리뷰존재하지않을때_삭제실패테스트() {
		// Given
		Long productCode = 100000001L;
		Long userId = 1L;
		Long reviewId = 1L;

		// Mocking
		when(productReviewRepository.findByIdAndProduct_ProductCodeAndUser_Id(reviewId, productCode, userId))
			.thenReturn(Optional.empty());

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewService.deleteReview(productCode, reviewId, userId),
			"리뷰를 찾을 수 없음 예외가 발생해야 합니다."
		);
	}

	@Test
	@DisplayName("이미지 업로드 실패 테스트 - 예외")
	public void 이미지업로드_실패테스트() throws IOException {
		// Given
		List<MultipartFile> images = Arrays.asList(mock(MultipartFile.class), mock(MultipartFile.class));
		List<String> s3ImageUrls = Arrays.asList("url1", "url2");

		ProductsReviewService productsReviewServiceSpy = spy(productsReviewService);

		// Mocking
		doThrow(IOException.class).when(productsReviewServiceSpy).uploadImageToS3(any(), any());

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewServiceSpy.uploadImagesToS3(images, s3ImageUrls),
			"이미지 업로드 실패 시 서버 에러 예외가 발생해야 합니다."
		);
	}

	@Test
	@DisplayName("다른 사용자가 리뷰 삭제할 때 실패 테스트 - 예외")
	public void 다른사용자가리뷰삭제할때_실패테스트() {
		// Given
		Long productCode = 100000002L;
		Long userId = 1L;
		Long reviewId = 22L;

		ProductReview existingReview = new ProductReview();
		existingReview.setId(reviewId);

		User user = new User();
		user.setId(userId);

		existingReview.setUser(new User());
		existingReview.setContent("Old Content");
		existingReview.setStarRate(StarRate.THREE);
		existingReview.setUpdatedAt(LocalDateTime.now());
		existingReview.setImagePath("oldImageUrl1 oldImageUrl2");

		List<String> prevS3Urls = Arrays.asList("oldImageUrl1", "oldImageUrl2");

		Optional<ProductReview> existingReviewOptional = Optional.of(existingReview);

		// Mocking
		when(productReviewRepository.findByIdAndProduct_ProductCodeAndUser_Id(reviewId, productCode, userId))
			.thenReturn(existingReviewOptional);
		when(imageParsing.getImageList("oldImageUrl1 oldImageUrl2")).thenReturn(prevS3Urls);
		when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(null);

		// When & Then
		assertThrows(BaseException.class,
			() -> productsReviewService.deleteReview(productCode, reviewId, userId),
			"리뷰를 다른 사용자가 삭제할 때 권한이 없음 예외가 발생해야 합니다."
		);
	}

}