package com.freeder.buclserver.app.products;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.domain.product.dto.ProductDTO;
import com.freeder.buclserver.domain.product.dto.ProductDetailDTO;
import com.freeder.buclserver.domain.productcategory.dto.ProductCategoryDTO;
import com.freeder.buclserver.domain.productoption.dto.ProductOptionDTO;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "products 관련 API", description = "상품 관련 API")
public class ProductsController {

	@Autowired
	private ProductsService productsService;

	@Autowired
	private ProductsCategoryService productsCategoryService;

	@GetMapping
	public ResponseEntity<BaseResponse<List<ProductDTO>>> getProducts(
		@RequestParam(defaultValue = "1") Long categoryId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int pageSize
	) {
		List<ProductDTO> products = productsService.getProducts(categoryId, page, pageSize);
		BaseResponse<List<ProductDTO>> response = new BaseResponse<>(products, HttpStatus.OK, "요청 성공");
		return ResponseEntity.ok(response);
	}

	@GetMapping("/category")
	public ResponseEntity<BaseResponse<List<ProductCategoryDTO>>> getProductsByCategory(
		@RequestParam(defaultValue = "1") Long categoryId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int pageSize
	) {
		List<ProductCategoryDTO> categoryProducts = productsCategoryService.getCategoryProducts(categoryId, page,
			pageSize);
		BaseResponse<List<ProductCategoryDTO>> response = new BaseResponse<>(categoryProducts, HttpStatus.OK, "요청 성공");
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<BaseResponse<ProductDetailDTO>> getProductDetail(@PathVariable Long productId) {
		ProductDetailDTO productDetail = productsService.getProductDetail(productId);
		BaseResponse<ProductDetailDTO> response = new BaseResponse<>(productDetail, HttpStatus.OK, "요청 성공");
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{productId}/options")
	public ResponseEntity<BaseResponse<List<ProductOptionDTO>>> getProductOptions(@PathVariable Long productId) {
		List<ProductOptionDTO> productOptions = productsService.getProductOptions(productId);
		BaseResponse<List<ProductOptionDTO>> response = new BaseResponse<>(productOptions, HttpStatus.OK, "옵션 요청 성공");
		return ResponseEntity.ok(response);
	}

}
