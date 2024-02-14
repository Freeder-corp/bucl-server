package com.freeder.buclserver.app.categories;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freeder.buclserver.app.products.ProductsCategoryService;
import com.freeder.buclserver.core.security.CustomUserDetails;
import com.freeder.buclserver.domain.productcategory.dto.ProductCategoryDTO;
import com.freeder.buclserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "categories 관련 API", description = "카테고리 관련 API")
public class CategoriesController {
	private final ProductsCategoryService productsCategoryService;

	@GetMapping("/{category_id}")
	@Transactional(readOnly = true)
	public BaseResponse<List<ProductCategoryDTO>> getProductsByCategory(
		@PathVariable(name = "category_id") Long categoryId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int pageSize,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		List<ProductCategoryDTO> categoryProducts = productsCategoryService.getCategoryProducts(categoryId, page,
			pageSize, userId);
		return new BaseResponse<>(categoryProducts, HttpStatus.OK, "요청 성공");
	}
}
