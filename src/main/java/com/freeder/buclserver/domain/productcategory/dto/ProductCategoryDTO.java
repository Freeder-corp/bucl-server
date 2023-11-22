package com.freeder.buclserver.domain.productcategory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCategoryDTO {
	private Long id;
	private String name;
	private String imagePath;
	private int salePrice;
	private int consumerPrice;
	private int reward;
	private int discountRate;
	private int totalReviewCount;
	private double averageRating;
}
