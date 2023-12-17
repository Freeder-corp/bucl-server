package com.freeder.buclserver.domain.product.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.freeder.buclserver.domain.productreview.dto.ReviewPreviewDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDetailDTO {
	private Long productCode;
	private String name;
	private String brandName;
	private int salePrice;
	private int consumerPrice;
	private float discountRate;
	private float averageRating;
	private LocalDateTime createdAt;
	private int totalReviewCount;
	private List<String> imagePaths;
	private List<String> detailImagePaths;
	private List<ReviewPreviewDTO> reviewPreviews;
	private boolean wished;
}
