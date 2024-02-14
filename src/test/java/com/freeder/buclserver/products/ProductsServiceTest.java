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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.freeder.buclserver.app.products.ProductsCategoryService;
import com.freeder.buclserver.app.products.ProductsService;
import com.freeder.buclserver.domain.product.dto.ProductDTO;
import com.freeder.buclserver.domain.product.entity.Product;
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

	@Test
	@DisplayName("상품 목록 조회 성공 테스트")
	void 상품목록조회_성공테스트() {
		// Given
		Long categoryId = 1L;
		int page = 0;
		int pageSize = 10;
		Long userId = 1L;

		Product product1 = new Product();
		product1.setId(1L);
		product1.setName("상품1");
		product1.setBrandName("브랜드1");
		product1.setProductCode(1L);

		Product product2 = new Product();
		product2.setId(2L);
		product2.setName("상품2");
		product2.setBrandName("브랜드2");
		product2.setProductCode(2L);

		List<Product> mockProductList = Arrays.asList(product1, product2);

		Page<Product> mockProductPage = new PageImpl<>(mockProductList);
		Optional<Page<Product>> mockProductPageOptional = Optional.of(mockProductPage);

		when(productRepository.findProductsByConditions(categoryId, PageRequest.of(page, pageSize)))
			.thenReturn(mockProductPageOptional);

		// When
		List<ProductDTO> result = productsService.getProducts(categoryId, page, pageSize, userId);

		// Then
		assertNotNull(result);
	}

	@Test
	@DisplayName("상품 목록 조회 실패 - BaseException 발생 테스트")
	void 상품목록조회_BaseException테스트() {
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
	void 상품목록조회_NullPointerException테스트() {
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
