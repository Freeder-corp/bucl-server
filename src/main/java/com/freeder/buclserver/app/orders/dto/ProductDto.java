package com.freeder.buclserver.app.orders.dto;

import java.util.List;

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.global.util.ProductUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductDto {
	//주문 상세보기 ->
	//주문 날짜
	//가격/수량
	//배송현황
	//구매확정
	//구매후기 작성
	// 상품사진
	// 브랜드명, 상품명
	private String productName; // 제품 이름
	private String productBrandName; // 브랜드 명
	private List<String> productImagePathList; // 제품 이미지 url;

	public static ProductDto from(Product product) {
		return new ProductDto(
			product.getName(),
			product.getBrandName(),
			ProductUtil.getImageList(product.getImagePath())
		);
	}
}
