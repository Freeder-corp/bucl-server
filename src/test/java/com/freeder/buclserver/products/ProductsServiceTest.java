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

@DisplayName("상품 서비스 조회 API")
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

	// @Test
	// @DisplayName("상품 목록 조회 성공 테스트")
	// void getProducts_Success() {
	// 	// Given
	// 	Long categoryId = 1L;
	// 	int page = 0;
	// 	int pageSize = 10;
	// 	Long userId = 1L;
	//
	// 	List<ProductDTO> mockProductList = Arrays.asList(
	// 		new ProductDTO(1L, "상품1", "브랜드1", "썸네일1", 10000, 8000, 2000.0f, false),
	// 		new ProductDTO(2L, "상품2", "브랜드2", "썸네일2", 20000, 15000, 5000.0f, true)
	// 	);
	//
	//
	// 	Page<ProductDTO> mockProductPage = new PageImpl<>(mockProductList);
	//
	// 	when(productRepository.findProductsByConditions(categoryId, PageRequest.of(page, pageSize)))
	// 		.thenReturn(mockProductPage);
	//
	// 	// When
	// 	List<ProductDTO> result = productsService.getProducts(categoryId, page, pageSize, userId);
	//
	// 	// Then
	// 	assertNotNull(result);
	// }

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

}
