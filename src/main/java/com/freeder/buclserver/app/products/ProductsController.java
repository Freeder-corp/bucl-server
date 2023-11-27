package com.freeder.buclserver.app.products;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.domain.product.dto.ProductDTO;
import com.freeder.buclserver.domain.product.dto.ProductDetailDTO;
import com.freeder.buclserver.domain.productcategory.dto.ProductCategoryDTO;
import com.freeder.buclserver.domain.productoption.dto.ProductOptionDTO;
import com.freeder.buclserver.domain.productreview.dto.ReviewPhotoDTO;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "products 관련 API", description = "상품 관련 API")
public class ProductsController {

	private final ProductsService productsService;
	private final ProductsCategoryService productsCategoryService;
	private final ProductsReviewService productsReviewService;
	private final ProductsReviewPhotoService productsReviewPhotoService;

	@Autowired
	public ProductsController(
		ProductsService productsService,
		ProductsCategoryService productsCategoryService,
		ProductsReviewService productsReviewService,
		ProductsReviewPhotoService productsReviewPhotoService
	) {
		this.productsService = productsService;
		this.productsCategoryService = productsCategoryService;
		this.productsReviewService = productsReviewService;
		this.productsReviewPhotoService = productsReviewPhotoService;
	}

	@GetMapping
	public BaseResponse<List<ProductDTO>> getProducts(
		@RequestParam(defaultValue = "1") Long categoryId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int pageSize
	) {
		List<ProductDTO> products = productsService.getProducts(categoryId, page, pageSize);
		return new BaseResponse<>(products, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/category")
	public BaseResponse<List<ProductCategoryDTO>> getProductsByCategory(
		@RequestParam(defaultValue = "1") Long categoryId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int pageSize
	) {
		List<ProductCategoryDTO> categoryProducts = productsCategoryService.getCategoryProducts(categoryId, page,
			pageSize);
		return new BaseResponse<>(categoryProducts, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/{productId}")
	public BaseResponse<ProductDetailDTO> getProductDetail(@PathVariable Long productId) {
		ProductDetailDTO productDetail = productsService.getProductDetail(productId);
		return new BaseResponse<>(productDetail, HttpStatus.OK, "요청 성공");
	}

	@GetMapping("/{productId}/options")
	public BaseResponse<List<ProductOptionDTO>> getProductOptions(@PathVariable Long productId) {
		List<ProductOptionDTO> productOptions = productsService.getProductOptions(productId);
		return new BaseResponse<>(productOptions, HttpStatus.OK, "옵션 요청 성공");
	}

	@GetMapping("/{productId}/reviews")
	public BaseResponse<ProductsReviewService.ProductReviewResult> getProductReviews(
		@PathVariable Long productId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int pageSize
	) {
		ProductsReviewService.ProductReviewResult result = productsReviewService.getProductReviews(productId, page,
			pageSize);
		return new BaseResponse<>(result, HttpStatus.OK, "리뷰 조회 성공");
	}

	@GetMapping("/{productId}/photos")
	public BaseResponse<List<ReviewPhotoDTO>> getReviewPhotos(
		@PathVariable Long productId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int pageSize,
		@RequestParam(defaultValue = "false") boolean more
	) {
		int adjustedPageSize = more ? 20 : pageSize;
		int startItemIndex = (page - 1) * adjustedPageSize + 1;

		List<ReviewPhotoDTO> reviewPhotos = productsReviewPhotoService.getProductReviewPhotos(productId, startItemIndex,
			adjustedPageSize);

		return new BaseResponse<>(reviewPhotos, HttpStatus.OK, "리뷰 사진 조회 성공");
	}

}
