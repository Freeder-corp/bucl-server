package com.freeder.buclserver.products;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.freeder.buclserver.app.products.ProductsCategoryService;
import com.freeder.buclserver.app.products.ProductsService;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.wish.repository.WishRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.util.ImageParsing;

@DisplayName("상품 서비스 조회 API 테스트")
public class ProductsServiceTest {

	@InjectMocks
	private ProductsService productsService;

	@Mock
	private ProductsCategoryService productsCategoryService;
	@Mock
	private ProductRepository productRepository;

	@Mock
	private WishRepository wishRepository;

	@Mock
	private ImageParsing imageParsing;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("상품 목록 조회 실패 - BaseException 발생 테스트")
	void getProducts_Failure_BaseException() {
		// Given
		Long categoryId = 1L;
		int page = 0;
		int pageSize = 10;
		Long userId = 1L;

		when(productRepository.findProductsByConditions(categoryId, PageRequest.of(page, pageSize)))
			.thenThrow(new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 에러"));

		// When / Then
		assertThrows(BaseException.class, () -> productsService.getProducts(categoryId, page, pageSize, userId));
	}

	@Test
	@DisplayName("상품 목록 조회 실패 - NullPointerException 발생 테스트")
	void getProducts_Failure_NullPointerException() {
		// Given
		Long categoryId = 1L;
		int page = 0;
		int pageSize = 10;
		Long userId = 1L;

		when(productRepository.findProductsByConditions(categoryId, PageRequest.of(page, pageSize)))
			.thenThrow(new NullPointerException("Null Point Access 에러"));

		// When / Then
		assertThrows(BaseException.class, () -> productsService.getProducts(categoryId, page, pageSize, userId));
	}

	// 추가적인 테스트 케이스를 필요에 따라 작성하십시오.
}
