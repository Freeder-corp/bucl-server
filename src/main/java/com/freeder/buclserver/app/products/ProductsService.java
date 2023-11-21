package com.freeder.buclserver.app.products;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freeder.buclserver.domain.product.dto.ProductDTO;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;

@Service
public class ProductsService {

	@Autowired
	private ProductRepository productRepository;

	public List<ProductDTO> getHotDealProducts(int page, int pageSize) {
		List<Product> hotDealProducts = productRepository.findHotDealProductsOrderByReward(page, pageSize);
		return hotDealProducts.stream()
			.map(this::convertToDTO)
			.collect(Collectors.toList());
	}

	public List<ProductDTO> getRewardProducts(int page, int pageSize) {
		List<Product> rewardProducts = productRepository.findRewardProductsOrderByReward(page, pageSize);
		return rewardProducts.stream()
			.map(this::convertToDTO)
			.collect(Collectors.toList());
	}

	private ProductDTO convertToDTO(Product product) {
		return new ProductDTO(
			product.getId(),
			product.getName(),
			product.getBrandName(),
			product.getImagePath(),
			product.getSalePrice(),
			product.getConsumerPrice(),
			product.getConsumerPrice() * product.getConsumerRewardRate()
		);
	}

}
