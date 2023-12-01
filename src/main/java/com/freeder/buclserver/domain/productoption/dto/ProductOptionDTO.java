package com.freeder.buclserver.domain.productoption.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductOptionDTO {
	private List<String> values;
	private int extraAmount;
}
