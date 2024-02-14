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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.freeder.buclserver.app.products.ProductsCategoryService;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.productcategory.dto.ProductCategoryDTO;
import com.freeder.buclserver.domain.productcategory.repository.ProductCategoryRepository;
import com.freeder.buclserver.domain.wish.repository.WishRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

@DisplayName("상품 카테고리 서비스 조회 API")
public class ProductsCategoryServiceTest {

	@Mock
	private ProductCategoryRepository productCategoryRepository;

	@Mock
	private WishRepository wishRepository;

	@Mock
	private ImageParsing imageParsing;

	@InjectMocks
	private ProductsCategoryService productsCategoryService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@DisplayName("카테고리 제품 조회 테스트 - 성공")
	public void 카테고리제품_조회성공테스트() {
		// Given
		Long categoryId = 1L;
		int page = 0;
		int pageSize = 10;
		Long userId = 1L;

		List<Product> products = Arrays.asList(
			new Product(),
			new Product()
		);

		// Mocking the Page
		when(productCategoryRepository.findProductsByCategory(categoryId, PageRequest.of(page, pageSize)))
			.thenReturn(Optional.ofNullable(new PageImpl<>(products)));
		when(imageParsing.getThumbnailUrl(anyString())).thenReturn("thumbnail-url");

		// When
		List<ProductCategoryDTO> result = productsCategoryService.getCategoryProducts(categoryId, page, pageSize,
			userId);

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());

		// Verify
		verify(productCategoryRepository, times(1)).findProductsByCategory(categoryId, PageRequest.of(page, pageSize));
	}

	@Test
	@DisplayName("카테고리 제품 조회 테스트 - 예외")
	public void 카테고리제품_조회실패테스트() {
		// Given
		Long categoryId = 999L;
		int page = 0;
		int pageSize = 10;
		Long userId = 123L;

		when(productCategoryRepository.findProductsByCategory(categoryId, PageRequest.of(page, pageSize)))
			.thenReturn(Optional.empty());

		// When & Then
		BaseException exception = assertThrows(BaseException.class,
			() -> productsCategoryService.getCategoryProducts(categoryId, page, pageSize, userId));
		assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
		assertEquals(404, exception.getErrorCode());
		assertEquals("해당 카테고리를 찾을 수 없음", exception.getErrorMessage());
	}
}
