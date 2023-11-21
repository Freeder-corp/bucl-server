package com.freeder.buclserver.app.products;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.domain.product.dto.ProductDTO;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "products 관련 API", description = "상품 관련 API")
public class ProductsController {

	@Autowired
	private ProductsService productsService;

	@GetMapping
	public ResponseEntity<BaseResponse<List<ProductDTO>>> getHotDealProducts(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int pageSize
	) {
		List<ProductDTO> hotDealProducts = productsService.getHotDealProducts(page, pageSize);
		BaseResponse<List<ProductDTO>> response = new BaseResponse<>(hotDealProducts, HttpStatus.OK, "요청 성공");
		return ResponseEntity.ok(response);
	}

	@GetMapping("/rewards")
	public ResponseEntity<BaseResponse<List<ProductDTO>>> getRewardProducts(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int pageSize
	) {
		List<ProductDTO> rewardProducts = productsService.getRewardProducts(page, pageSize);
		BaseResponse<List<ProductDTO>> response = new BaseResponse<>(rewardProducts, HttpStatus.OK, "요청 성공");
		return ResponseEntity.ok(response);
	}
}
