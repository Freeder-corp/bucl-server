package com.freeder.buclserver.products;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.freeder.buclserver.app.products.ProductsCategoryService;
import com.freeder.buclserver.domain.productcategory.repository.ProductCategoryRepository;
import com.freeder.buclserver.domain.wish.repository.WishRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

@DisplayName("상품 카테고리 서비스 테스트")
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
	@DisplayName("카테고리 제품 조회 실패 테스트")
	public void getCategoryProducts_NotFound() {
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
