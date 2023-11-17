package com.freeder.buclserver.app.products;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(params = "/api/v1/products")
@Tag(name = "products 관련 API", description = "상품 관련 API")
public class ProductsController {
}
